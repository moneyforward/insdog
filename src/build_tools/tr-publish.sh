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

function exec_trcli() {
  local _report_file="${1}" _title="${2}" _run_description="${3}"
  trcli -y -h "$(tr_url)"    \
    --project "$(tr_project)"   \
    -u "$(tr_username)"  \
    -p "$(tr_password)"   \
    parse_junit   \
    -f "${_report_file}" \
    --title "${_title}"   \
    --run-description "${_run_description}"
}

function perform() {
  local _report_file="${1}" _title="${2}" _run_description=${3}

  function tr_url() {
    echo "https://moneyforward.tmxtestrail.com/"
  }

  function tr_project() {
    echo "autotest-ca"
  }

  function tr_suite_name() {
    echo "UT-2"
  }
  exec_trcli "${_report_file}" "${_title}" "${_run_description}"
}

function main() {
  local _report_file="${1}" _title="${2:-unknown}" _run_description="${3:-None}"
  perform "${_report_file}" "${_title}" "${_run_description}"
}

main "${@}"