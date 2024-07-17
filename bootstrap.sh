#!/bin/bash -eu

function sed() {
  /usr/bin/sed "${@}"
}

function grep() {
  /usr/bin/grep "${@}"
}

function projectbrew() {
  "${brewdir}/bin/brew" "${@}"
}

function install_project_homebrew() {
  local _homebrew_dir="${1}"
  mkdir -p "${_homebrew_dir}"
  git init "${_homebrew_dir}"
  git -C "${_homebrew_dir}" remote add "origin" https://github.com/Homebrew/brew
  # git checkout -b main -C "${_homebrew_dir}"
  curl -L https://github.com/Homebrew/brew/tarball/master | tar xz --strip 1 -C "${_homebrew_dir}"
  # rm -fr "$("${_homebrew_dir}/bin/brew" --repo homebrew/core)"

  git -C "${_homebrew_dir}" add --all .
  git -C "${_homebrew_dir}" commit -a -m 'DUMMY COMMIT' >& /dev/null
}


function bootstrap() {
  local _homebrew_dir="${1}"
  install_project_homebrew "${_homebrew_dir}" | progress "BOOTSTRAP" 2>&1 /dev/null
}

function install_brew_package() {
  local _homebrew_dir="${1}" _package="${2}"
  projectbrew install "${2}" | tee >(progress "${_package}")
}

function sdk_install() {
  local _lang="${1}" _ver="${2}"
  bash -c 'source '"${SDKMAN_DIR}"'/bin/sdkman-init.sh
           sdk install '"${_lang}"' '"${_ver}"
}

function progress() {
  local _item_name="${1}"
  echo "BEGIN: ${_item_name}" >&2
  sed -E 's/^(.*)$/  ['"${_item_name}"'] \1/g' >&2
  echo "END:   ${_item_name}" >&2
}


function caveats() {
  # FIXME
  # brew install uses unusual line breaks in Caveats.
  # To ensure comment-out happens correctly, we do the sed twice.
  sed -n '/==> Caveats/,/END/p' | grep -v '==>'            \
                                | grep -v '🍺'             \
                                | grep -E '[A-Z_0-9]+='
}

function reset_caveats_rc() {
  local _fname="${1}"
  [[ -e "${_fname}" ]] && rm "${_fname}"
  touch "${_fname}"
}

function compose_goenv_rc() {
  local _project_godir="${1}" _go_version="${2}"
  echo "
  export GOENV_ROOT=${_project_godir}/env
  export GOENV_SHELL=bash
  export GOPATH=${_project_godir}/${_go_version}
  export GOROOT=${_project_godir}/env/versions/${_go_version}

  export PATH=${_project_godir}/${_go_version}/bin:${PATH}
  "
}

function message() {
  echo "${@}" >&2
}

function main() {
  local _projectdir _project_dependencies_dir _project_brewdir  _project_rcdir _caveats_file
  local _project_godir _goenv_file _go_version
  local _project_sdkman_dir
  _projectdir="$(dirname "${1}")"
  _projectdir="$(realpath "${_projectdir}")"
  _project_dependencies_dir="${_projectdir}/.dependencies"

  _project_brewdir="${_project_dependencies_dir}/homebrew"

  _project_rcdir="${_project_dependencies_dir}/rc"
  _caveats_file="${_project_dependencies_dir}/rc/caveats.rc"

  _goenv_file="${_project_dependencies_dir}/rc/goenv.rc"
  _go_version="1.21.6"
  _project_godir="${_project_dependencies_dir}/go"

  _project_sdkman_dir="${_project_dependencies_dir}/sdkman"
  shift

  mkdir -p "${_project_rcdir}"
  bootstrap "${_project_brewdir}"
  reset_caveats_rc "${_caveats_file}"

  export HOMEBREW_NO_AUTO_UPDATE=0
  # Disable this behaviour by setting HOMEBREW_NO_INSTALL_CLEANUP.
  # Hide these hints with HOMEBREW_NO_ENV_HINTS (see `man brew`).
  export HOMEBREW_NO_INSTALL_CLEANUP=0
  export HOMEBREW_NO_ENV_HINTS=0

  # shellcheck disable=SC2129
  install_brew_package "${_project_brewdir}" make       | caveats >> "${_caveats_file}"
  install_brew_package "${_project_brewdir}" gnu-sed    | caveats >> "${_caveats_file}"
  install_brew_package "${_project_brewdir}" findutils  | caveats >> "${_caveats_file}"

  # golang
  mkdir -p "${_project_godir}/env"
  install_brew_package "${_project_brewdir}" goenv   > /dev/null
  compose_goenv_rc "${_project_godir}" "${_go_version}" > "${_goenv_file}"
  # shellcheck disable=SC1090
  source "${_goenv_file}"
  "${_project_brewdir}/bin/goenv" install -q "${_go_version}" 2>&1 | progress "goenv:${_go_version}"
  "${_project_godir}/env/versions/${_go_version}/bin/go" install github.com/Songmu/make2help/cmd/make2help@latest 2>&1 | progress "go:make2help"

  # sdkman & Java
  export HOME="${_project_rcdir}" # To avoid .bashrc / .bash_profile / .zsh_profile being updated
  export SDKMAN_DIR="${_project_sdkman_dir}"
  # install sdkman
  curl -s "https://get.sdkman.io" | /bin/bash 2>&1 | progress "sdkman"

  sdk_install java 21.0.2-open | progress "sdkman:openjdk"
  sdk_install maven 3.9.6      | progress "sdkman:maven"
}

projectdir="$(dirname "${BASH_SOURCE[0]}")"
projectdir="$(realpath "${projectdir}")"
brewdir="${projectdir}/.dependencies/homebrew"

main "${0}" "$@"

