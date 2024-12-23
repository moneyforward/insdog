#!/usr/bin/env bash

set -E -o nounset -o errexit +o posix -o pipefail
shopt -s inherit_errexit

main() {
  local _indir="${1}" _outdir="${2}" _resources_dir="${3}"
  local _i
  local _convert_links
  _convert_links="$(dirname "${0}")/convert_links.lua"
  for _i in $(find "${_indir}" -type f -name '*.md' -printf "%P\n"); do
    local _outfile
    _outfile="${_outdir}/$(echo "${_i}"|sed -E "s/.md$/.html/g")"
    mkdir -p "$(dirname "${_outdir}/${_i}")"
    cat "${_resources_dir}/header.html"                >  "${_outfile}"
    pandoc --lua-filter="${_convert_links}" --from markdown --to html "${_indir}/${_i}" >> "${_outfile}"
    cat "${_resources_dir}/footer.html"                >> "${_outfile}"
  done
}
main "${@}"