#!/usr/bin/env bash

set -E -o nounset -o errexit +o posix -o pipefail
shopt -s inherit_errexit


function abort() {
  message "${@}"
  exit 1
}

function message() {
  echo "${@}" >&2
}
export -f message

function tr_username() {
  abort "undefined function"
}

function tr_password() {
  abort "undefined function"
}

function tr_url() {
  abort "undefined function"
}

function tr_project() {
  abort "undefined function"
}

function tr_suite_name() {
  abort "undefined function"
}

function exec_trcli() {
  local _test_report_file="${1}" _title="${2}" _run_description="${3}"
  trcli -y -h "$(tr_url)"    \
    --project "$(tr_project)"   \
    --suite-name "$(tr_suite_name)"     \
    -u "$(tr_username)"  \
    -p "$(tr_password)"   \
    parse_junit   \
    -f "${_test_report_file}" \
    --title "${_title}"   \
    --run-description "Local run (Code First)"
}

function linef() {
  local _format="${1:-""}"
  shift
  # shellcheck disable=SC2059
  printf "${_format}\n" "${@}"
}

function encode_string_to_xml_attribute() {
  local _content="${1}"
  echo "${_content}" | sed -e 's/&/\&amp;/g' \
                           -e 's/</\&lt;/g' \
                           -e 's/>/\&gt;/g' \
                           -e 's/"/\&quot;/g' \
                           -e "s/'/\&apos;/g"
}

function begin_report_xml() {
  echo '<testsuites name="unnamed">'
}

function end_report_xml() {
  echo '</testsuites>'
}

function count_test_result() {
  local _test_result_directory="${1}" _regex_for_test_result="${2}"
  find "${_test_result_directory}" -name 'RESULT' -exec grep -E "${_regex_for_test_result}" {} \; | wc -l
}

function compose_testsuite_name() {
  local _test_suite_result_dir="${1}"
  basename "${_test_suite_result_dir}"
}

function test_execution_time() {
  echo "123"
}

function compose_test_suite() {
  local _test_suite_result_dir="${1}"
  linef '  <testsuite failures="%s" errors="%s" skipped="%s" tests="%s" time="%s" name="%s">' \
         "$(count_test_result "${_test_suite_result_dir}" 'RESULT: FAILED')" \
         "$(count_test_result "${_test_suite_result_dir}" 'RESULT: ERROR')" \
         "$(count_test_result "${_test_suite_result_dir}" 'RESULT: SKIPPED')" \
         "$(count_test_result "${_test_suite_result_dir}" 'TYPE: TEST')" \
         "$(test_execution_time)" \
         "$(compose_testsuite_name "${_test_suite_result_dir}")"
  local _each _id=0
  for _each in "${_test_suite_result_dir}/"*; do
    if [[ -d "${_each}" ]]; then
      compose_testcase_element "${_each}" "${_id}"
      _id="$((_id + 1))"
    fi
  done
  linef '  </testsuite>'
}

function compose_test_case_attribute_value() {
  local _filename="${1}"
  if [[ "${_filename}" = *.log ]]; then
    local _content
    _content="$(cat "${_filename}")"
  else
    linef '<property name="testrail_attachment" value="'"${_filename}"'"/>'
  fi
}

function compose_testclass_name() {
  local _testcase_dir="${1}"
  local _out
  _out="$(dirname "${_testcase_dir}")"
  basename "${_out}"
}

function compose_testcase_name() {
  local _testcase_dir="${1}"
  local _short_testcase_name
  local _testcase_name
  _short_testcase_name="$(compose_testclass_name "${_testcase_dir}")"
  _short_testcase_name="${_short_testcase_name##*.}"
  _testcase_name="$(basename "${_testcase_dir}")"
  echo "${_short_testcase_name}.${_testcase_name}"
}

function compose_testcase_time() {
  local _testcase_dir="${1}"
  local _ret
  _ret="$(grep -E '^TIME:' "${_testcase_dir}/RESULT" 2> /dev/null || :)"
  echo "${_ret#TIME: }"
}

function compose_testcase_result() {
  local _testcase_dir="${1}"
  local _ret
  _ret="$(grep -E '^RESULT:' "${_testcase_dir}/RESULT" 2> /dev/null || :)"
  echo "${_ret#RESULT: }"
}

function compose_markdown_section_from_logfiles() {
  local _testcase_dir="${1}"

  local _each
  # shellcheck disable=SC2016
  linef '# Log Files in `%s`' "$(compose_testcase_name "${_testcase_dir}")"
  echo
  for _each in $(list_files_under "${_testcase_dir}" "*.log"); do
    # shellcheck disable=SC2016
    linef '## `%s`' "$(basename "${_each}")"
    echo
    echo "  Log Entries:"
    sed -E 's/^(.*)/  \1/g' "${_each}"
    echo
  done
}

function list_files_under() {
  local _directory="${1}" _glob="${2}"
  # shellcheck disable=SC2086
  ls -rt "${_directory}"/${_glob} 2> /dev/null || :
}

function compose_testcase_element() {
  local _testcase_dir="${1}" _id="${2}"

  local _classname _name _time _id _comment
  _classname="$(compose_testclass_name "${_testcase_dir}")" # tests.LoginTests
  _name="$(compose_testcase_name "${_testcase_dir}" )"
  _time="$(compose_testcase_time "${_testcase_dir}")"
  _comment="$(compose_markdown_section_from_logfiles "${_testcase_dir}")"
  _comment="$(encode_string_to_xml_attribute "${_comment}")"
  _result="$(compose_testcase_result "${_testcase_dir}")"
  linef '<testcase classname="%s" name="%s" time="%s" id="%s">' "${_classname}" "${_name}" "${_time}" "${_id}"
  if [[ "${_result}" != SUCCESSFUL ]]; then
    if [[ "${_result}" == "SKIPPED" ]]; then
      echo '<skip type="SKIPPED" message "test skipped">'
      echo "${_comment}"
      echo '</skip>'
    elif [[ "${_result}" == "FAILED" ]]; then
      echo '<failure type="FAILED" message="failed">'
      echo "${_comment}"
      echo '</failure>'
    else
      echo '<failure type="UNKNOWN" message="UNKNOWN('"${_result}"')">'
      echo "${_comment}"
      echo '</failure>'
    fi

  fi
  linef '  <properties>'
  local _each
  for _each in $(list_files_under "${_testcase_dir}" "*.png"); do
    linef '    <property name="testrail_attachment" value="%s"/>' "${_each}"
  done
  linef '  </properties>'
  linef '</testcase>'
}

function compose_report_xml() {
  local _test_result_directory="${1}"
  begin_report_xml
  local _i
  for _i in "${_test_result_directory}/"*; do
    compose_test_suite "${_i}"
  done
  end_report_xml
}

function perform() {
  local _test_result_directory="${1}" _title="${2}" _run_description=${3}
  local _report_file
  _report_file="$(mktemp)"

  function tr_username() {
    echo "ukai.hiroshi@moneyforward.co.jp"
  }

  function tr_password() {
    echo "<PASSWORD>"
  }

  function tr_url() {
    echo "https://moneyforward.tmxtestrail.com/"
  }

  function tr_project() {
    echo "autotest-ca"
  }

  function tr_suite_name() {
    echo "UT-2"
  }
  compose_report_xml "${_test_result_directory}" > "${_report_file}"

  exec_trcli "${_report_file}" "${_title}" "${_run_description}"
}

function main() {
  # compose_test_case_element "/Users/ukai.hiroshi/Documents/github/moneyforward/autotest-ca/target/testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/1_データ連携" "099"
  compose_report_xml "/Users/ukai.hiroshi/Documents/github/moneyforward/autotest-ca/target/testResult"
}

main "${@}"