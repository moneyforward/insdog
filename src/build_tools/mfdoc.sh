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

function _render_index_md_content() {
  local _dirname="${1}" _basedir="${2}"
  local _filenames _i _ret="" _f="${_dirname}/README.md"
  _ret="$(_auto_generation_warning)"
  if [[ -e "${_f}" ]]; then
    _ret="$(printf "%s\n\n%s" "${_ret}" "$(cat "${_f}")")"
    _ret="$(printf "%s\n\n" "${_ret}")"
  fi
  _ret="$(printf "%s\n\n## Files\n\n" "${_ret}")"
  if [[ "${_dirname}" != "${_basedir}" ]]; then
    _ret="$(printf "%s\n- [..](../index.md)\n" "${_ret}")"
  fi
  mapfile -t _filenames < <(ls "${_dirname}")
  for _i in "${_filenames[@]}"; do
    local _subject _link
    if [[ "${_i}" == "index.md" || "${_i}" == "README.md" || "${_i}" == "_Sidebar.md" || "${_i}" == "_Home.md" ]]; then
      continue
    fi
    _link="$(_resolve_linked_path "${_basedir}" "${_dirname}" "${_i}")"
    _subject="$(_extract_subject "${_basedir}" "${_dirname}" "${_i}")"
    _ret="$(printf "%s\n- [%s](%s)" "${_ret}" "${_subject}" "${_link}")"
  done
  echo "${_ret}"
}

function _auto_generation_warning() {
  echo '<!--
    DO NOT EDIT: This file is generated automatically.
  -->'
}
export -f _auto_generation_warning

function _extract_subject() {
  local _basedir="${1}" _dir="${2}" _f="${3}"
  local _filename="${_basedir}/${_dir}/${_f}"
  local _fallback
  _fallback="$(basename "${_filename}")"
  _fallback="${_fallback%.md}"
  _ret="$(grep '^#[^#].+$' "${_filename}" 2> /dev/null | head --lines=1 || echo "${_fallback}")"
  echo "${_ret}"
}


function _resolve_linked_path() {
  local _basedir="${1}" _dir="${2}" _f="${3}"
  local _filename="${_basedir}/${_dir}/${_f}"
  if [[ "${_filename}" == *.md ]]; then
    basename "${_filename}"
  else
    echo "$(basename "${_filename}")/index.md"
  fi
}

# Converts a file specified by "{_src_basedir}/path/to/file.md}" for github wiki and write it to "{_dest_basedir}/path|to|file.md".
#
# @param _src_basedir A base directory under which source markdown files are stored.
# @param _dest_basedir A base directory under which generated markdown files are stored.
# @param _path_to_src_file A relative path to a file to be processed from src_basedir.
function _to_github_md_file() {
  local _src_basedir="${1}" _dest_basedir="${2}" _path_to_src_file="${3}"
  local _src_filename="${_path_to_src_file}"
  local _dest_filename _p="${_path_to_src_file#"${_src_basedir}"/}"
  _dest_filename="${_dest_basedir}/${_p//\//|}"
  _dest_filename="${_dest_filename/|index.md/.md}"
  _auto_generation_warning > "${_dest_filename}"
  _mangle_links_in_source "${_src_basedir}" "${_path_to_src_file}" < "${_src_filename}" >> "${_dest_filename}"
}

function _mangle_links_in_source() {
  local _src_basedir="${1}" _path_to_src_file="${2}"
  local _dirname_for_src_file _linkbase _parent
  _dirname_for_src_file="${_path_to_src_file}"
  _dirname_for_src_file="$(dirname "${_dirname_for_src_file}")"
  # _dirname_for_src_file="$(dirname "${_dirname_for_src_file}")"
  _linkbase="${_dirname_for_src_file}"
  _linkbase="${_linkbase#"${_src_basedir}"/}"
  if [[ "${_linkbase}" == "${_src_basedir}" ]]; then
    _linkbase=""
  else
    _linkbase="${_linkbase}|"
  fi
  _linkbase="${_linkbase//\//|}"
  if [[ "${_linkbase}" == *"|"* ]]; then
    _parent="${_linkbase%|*}"
  else
    _parent="index"
  fi

  if [[ "${_path_to_src_file}"  == */index.md ]]; then
    message "_linkbase=<${_linkbase}>: _src_basedir=<${_src_basedir}> path_to_src_file=<${_path_to_src_file}>"
  fi

  # \1 link text
  # \2 link target
  sed -E 's/\[(.+)\]\(([^:]+).md\)/[\1]('"${_linkbase}"'\2)/g' |
  sed -E 's/\[(.+)\]\(([^:]+)\/index\)/[\1](\2)/g'|
  sed -E 's/\[(.+)\]\(([^:]+)\|\.\.\)/[\1](\2)/g'|
  sed -E 's/\[\.\.\]\(([^:]+)\)/[..]('"${_parent}"')/g'
}
export -f _mangle_links_in_source

# - generate indices for `.work/doc`
function _generate_indices() {
  local _basedir="${1}"
  local _dirs _i
  mapfile -t _dirs < <(find "${_basedir}" -type d | grep -v '.git' | sort -r)
  for _i in "${_dirs[@]}"; do
    local _out="index.md"
    _render_index_md_content "${_i}" "${_basedir}" > "${_i}/${_out}"
  done
}

# This function generates a mkdocs.yml from already generated techdocs files.
function _generate_mkdocs_yml() {
  local _techdocs_dir="${1}"
  {
    echo "site_name: ${_project_name}"
    echo "nav:"
    echo "  - index.md"
    mapfile -t _dirs < <(ls "${_techdocs_dir}/"*"/index.md")
    for _i in "${_dirs[@]}"; do
      echo "  - ${_i#${_techdocs_dir}/}"
    done
  } >> "${_techdocs_dir}/mkdocs.yml"
}

function _generate_index_for_manually_written_wiki_pages() {
  local _wiki_dir="${1}"
  mapfile -t _wiki_pages < <(find "${_wiki_dir}" -maxdepth 1 -not -type d | sed -E 's/\.md$//')
  _auto_generation_warning
  echo "# Wiki pages"
  for _i in "${_wiki_pages[@]}"; do
    local _f="${_i##*/}"
    if [[ "${_i}" == *Sidebar* ]]; then
      continue
    fi
    echo "- [${_f}](${_f})"
  done
}

# Converts a file specified by "{_src_basedir}/path/to/file.md}" for techdocs markdown and write it to "{_dest_basedir}/path|to|file.md".
# This implementation just adds an auto-generation warning to each file.
#
# @param _src_basedir A base directory under which source markdown files are stored.
# @param _dest_basedir A base directory under which generated markdown files are stored.
# @param _path_to_src_file A relative path to a file to be processed from src_basedir.
function _to_techdocs_md_file() {
  local _src_basedir="${1}" _dest_basedir="${2}" _path_to_src_file="${3}"
  local _src_filename="${_path_to_src_file}"
  local _dest_filename _p="${_path_to_src_file#"${_src_basedir}"/}"
  _dest_filename="${_dest_basedir}/${_p}"
  mkdir -p "$(dirname "${_dest_filename}")"
  _auto_generation_warning > "${_dest_filename}"
  cat "${_path_to_src_file}" >> "${_dest_filename}"
}

# Copy files whose names match $1 (glob) under a directory specified by $2 unto $3 keeping the directory structure.
#
# Example:
#   Given:
#     destdir/
#       X/
#         a/
#           file.txt
#           file.md
#           b/
#             file.md
#             file.txt
#   When:
#     _copy_files "*.md" srcdir/X destdir/Y
#   Then:
#     destdir/
#       Y/
#         a/
#           file.md
#           b/
#             file.md
function _copy_files() {
  local _files="${1:-*}"    _src_base="${2:-doc}"              _dest_base="${3:-.work/doc}"              # .work/doc/...
  find "${_src_base}" -type f -name "${_files}" \
        -exec sh -c '_rel_dest="${1#${2}/}" &&
        mkdir -p "${3}/$(dirname "${_rel_dest}")" &&
        cp "${1}" "${3}/${_rel_dest}"' \
        "DUMMY" {}  "${_src_base}" "${_dest_base}" \;
}

function _clone_wiki() {
  local _wiki_dir="${1}"
  # - remove the entire `.work/wiki`.
  [[ -e "${_wiki_dir}" ]] && rm -fr "${_wiki_dir}"
  # - clone `{repo_name}.wiki.git` repo to `.work/wiki/`
  local _repo_url _wiki_repo_url
  _repo_url="$(git config --get remote.origin.url)"
  _wiki_repo_url="${_repo_url%.git}.wiki.git"
  git clone --depth=1 "${_wiki_repo_url}" --branch master --single-branch "${_wiki_dir}"
}

function _clone_techdocs() {
  local _techdocs_dir="${1}"
  # - remove the entire `.work/wiki`.
  [[ -e "${_techdocs_dir}" ]] && rm -fr "${_techdocs_dir}"
  # - clone `{repo_name}.git`'s techdocs branch to `.work/techdocs/`
  local _repo_url _techdocs_repo_url
  _repo_url="$(git config --get remote.origin.url)"
  _techdocs_repo_url="${_repo_url}"
  git clone --depth=1 "${_techdocs_repo_url}" --branch techdocs --single-branch "${_techdocs_dir}" || abort "branch: 'techdocs' was not found in this repository."
}

function _empty_compiled_doc_dir() {
  local _dir_for_staged_wiki_files="${1?_dir_for_staged_wiki_files is not specified!}"
  # - empty the entire `"${_wiki_dir}/doc`.
  [[ -e "${_dir_for_staged_wiki_files}" ]] && rm -fr "${_dir_for_staged_wiki_files}"
  mkdir -p "${_dir_for_staged_wiki_files}"
}

function _render_github_wiki_files() {
  local _doc_dest_dir="${1}" _wiki_dir="${2}"
  _render_doc_files "${_doc_dest_dir}" "${_wiki_dir}" "_to_github_md_file" "doc"
}

function _render_techdocs_files() {
  local _doc_dest_dir="${1}" _techdocs_dir="${2}"
  _render_doc_files "${_doc_dest_dir}" "${_techdocs_dir}" "_to_techdocs_md_file" "docs"
}

function _render_doc_files() {
  local _doc_dest_dir="${1}" _output_dir="${2}" _renderer_function_name="${3}" _docdir_under_output_dir="${4}"
  # - convert `.md` files under `.work/doc` and put the converted ones under `.work/wiki/doc/`
  #   slashes (`/`) in a relative path name of a `.md` file will be converted into pipes (`|`).
  #   links in every file is mangled unless the destination contains a colon.
  mapfile -t _md_files < <(find "${_doc_dest_dir}" -type f -name '*.md')
  local _i
  for _i in "${_md_files[@]}"; do
    "${_renderer_function_name}" "${_doc_dest_dir}" "${_output_dir}/${_docdir_under_output_dir}" "${_i}"
  done
}

function clean() {
  local _wiki_dir="${1?_wiki_dir is not specified}"
  local _techdocs_dir="${2?_doc_dest_dir is not specified!}"
  local _doc_dest_dir="${3?_tecdocs_dir is not specified!}"
  rm -fr "${_doc_dest_dir}"
  rm -fr "${_techdocs_dir}"
  rm -fr "${_wiki_dir}"
}

# This function does a "step-1" compilation of the documentation and this is in common for the techdocs and the wiki
# site generations.
#
# In this step the following things will be done.
# 1. Copies files based on the directives givens as the second and the following parameter values.
#    A directive looks like "{pattern}:{src}:{dest}"
#    The pattern is a glob, src is a directory's relative path from the project's root directory and the `dest` is a
#    relative path from the `_doc_dest_dir`.
# 2. Generates indices (generated) files under `_doc_dest_dir` (, which is specified by the first argument).
#
# See also _copy_files function.
# See also _generate_indices function.
function compile-docs() {
  local _doc_dest_dir="${1}"
  shift
  local _doc_dirs=("${@}")
  message "BEGIN: compile-docs"
  for _i in "${_doc_dirs[@]}"; do
    readarray -d ':' -t _arr < <(echo -n "${_i}")
    local _files="${_arr[0]}" _src="${_arr[1]}" _dest="${_arr[2]:-""}"
    _copy_files "${_files}" "${_src}" "${_doc_dest_dir}/${_dest}"
  done
  _generate_indices "${_doc_dest_dir}"
  message "END: compile-docs"
}

function compile-wiki() {
  local _wiki_dir="${1}" _doc_dest_dir="${2}" _dir_for_staged_wiki_files="${3}"
  message "compile-wiki"

  _clone_wiki "${_wiki_dir}"
  _empty_compiled_doc_dir "${_dir_for_staged_wiki_files}"

  _render_github_wiki_files "${_doc_dest_dir}" "${_wiki_dir}"
  _generate_index_for_manually_written_wiki_pages "${_wiki_dir}" > "${_wiki_dir}/_Sidebar.md"
  cat "${_dir_for_staged_wiki_files}/index.md" >> "${_wiki_dir}/_Sidebar.md"
}

function compile-techdocs() {
  local _techdocs_dir="${1}" _doc_dest_dir="${2}" _dir_for_generated_docs_in_techdocs="${3}"

  _clone_techdocs "${_techdocs_dir}"
  _empty_compiled_doc_dir "${_dir_for_generated_docs_in_techdocs}"
  _render_techdocs_files "${_doc_dest_dir}" "${_techdocs_dir}"
  _generate_mkdocs_yml "${_doc_dest_dir}"
}

function publish-wiki() {
  local _wiki_dir="${1}"
  _publish-docs "${_wiki_dir}" master
}

function publish-techdocs() {
  local _techdocs_dir="${1}"
  _publish-docs "${_techdocs_dir}" techdocs
}

function _publish-docs() {
  local _staged_dir="${1}" _branch="${2}"
  # - add/commit/push generated wiki to remote.
  #   don't worry, we are not pushing it to the product repo, but to the product.wiki repo.
  git  -C "${_staged_dir}" add --all
  # Some Differentiation would be preferable. summary from
  git  -C "${_staged_dir}" commit -a -m "$(printf 'UPDATE:%s' "$(git log -n 1 --format='%s (%h)
----
commit %H
Author: %an
Date: %ad

%s

%b
----')")" || :
  git  -C "${_staged_dir}" push origin "${_branch}:${_branch}"
}

function _parse_subcommands() {
  local _i
  for _i in "${@}"; do
    if [[ "${_i}" == "--" ]]; then
      break;
    fi
    if [[ "${_i}" == "--"* ]]; then
      continue
    fi
    echo "${_i}"
  done
}


# Parses the given arguments and prints option values.
#
# [0]: --local-wiki-dir=:
#                         Directory to store wiki site's contents.
#                         (default: .work/wiki)
# [1]: --local-techdocs-dir=:
#                         Directory to store techdocs site's contents.
#                         (default: .work/techdocs)
# [2]: --local-doc-dir=:  Directory to store documentation files in this repository temporarily.
#                         (default: .work/doc)
# [3]: --generated-doc-base-in-local-wiki-dir:
#                         Relative path from --local-wiki-dir= to a directory which stores
#                         documentation files in this repository.
#                         (default: doc)
# [4]: --generated-doc-base-in-local-techdocs-dir
#                         Relative path from --local-doc-dir= to a directory which stores
#                         documentation files in this repository.
#                         (default: docs)
function _parse_options() {
  local _i
  local _s="subcommands"
  local _local_wiki_dir=".work/wiki" _local_techdocs_dir=".work/techdocs" _local_doc_dir=".work/doc"
  local _generated_doc_base_in_local_wiki_dir="doc" _generated_doc_base_in_local_techdocs_dir="docs"
  for _i in "${@}"; do
    if [[ "${_i}" == "--" ]]; then
      if [[ "${_s}" == "subcommands" ]]; then
        _s="mappings"
        continue
      elif [[ "${_s}" == "mappings" ]]; then
        _s="options"
        continue
      else
        abort "unknown state: ${_s}"
      fi
    fi
    if [[ "${_s}" != "options" ]]; then
      continue
    fi
    if [[ "${_i}" == "--"* ]]; then
      if [[ "${_i}" == "--local-wiki-dir="* ]]; then
        _local_wiki_dir="${_i#*=}"
      elif [[ "${_i}" == "--local-techdocs-dir="* ]]; then
        _local_techdocs_dir="${_i#*=}"
      elif [[ "${_i}" == "--local-doc-dir="* ]]; then
        _local_doc_dir="${_i#*=}"
      elif [[ "${_i}" == "--generated-doc-base-in-local-wiki-dir="* ]]; then
        _generated_doc_base_in_local_wiki_dir="${_i#*=}"
      elif [[ "${_i}" == "--generated-doc-base-in-local-techdocs-dir="* ]]; then
        _generated_doc_base_in_local_techdocs_dir="${_i#*=}"
      fi
    fi
  done
  echo "${_local_wiki_dir}"
  echo "${_local_techdocs_dir}"
  echo "${_local_doc_dir}"
  echo "${_generated_doc_base_in_local_wiki_dir}"
  echo "${_generated_doc_base_in_local_techdocs_dir}"
}

# Parses the given arguments and prints the directory mappings.
#
#  "*.md:tools:packages/tools"
#   ^    ^     ^
#   |    |     |
#   |    |     +----- relative path from --local-doc-dir to a directory to copy target files to.
#   |    +----------- relative path from the project's root to the directory that stores target files.
#   +---------------- target file patterns.
function _parse_directory_mappings() {
  local _i
  local _s="subcommands"
  for _i in "${@}"; do
    if [[ "${_i}" == "--" ]]; then
      _s="mappings"
      continue
    fi
    if [[ "${_s}" == "subcommands" ]]; then
      continue
    fi
    echo "${_i}"
  done
}

# Directory Layout:
# - `.work/`: working directory
#   A directory for storing automatically generated temporary files.
#   Any contents under here shouldn't be registered in git.
#   - `doc/`: doc-working directory
#     A directory that temporary stores working copy of `{project_root}/doc/` directory.
#     packages/
#     A directory that collects documentations written as `.md` files in source code packages
#     based on the "mappings".
#   - `wiki/`: wiki-staging directory
#     A directory that clones the content of `{repo_name}.wiki` git repository's `master` branch's top.
#     - `doc/`
#       A directory that stores github wiki converted files. (a/b/c.md => a|b|c, links are mangled)
#   - `techdocs/`: techdocs-staging directory
#     - `docs/`
#       A directory that stores files to be published as techdocs contents.
function main() {
  mapfile -t _subcommands < <(_parse_subcommands "${@}")
  mapfile -t _mappings < <(_parse_directory_mappings "${@}")
  mapfile -t _options < <(_parse_options "${@}")
  [[ "${#_subcommands[@]}" == 0 ]] && _subcommands=(clean compile-wiki compile-techdocs)
  # - check if .git/config exists. exit if not.
  [[ -f .git/config ]] || abort "This directory seems not to be a project root directory."
  local _pwd
  _pwd="$(pwd)"
  local _wiki_dir="${_options[0]}" _techdocs_dir="${_options[1]}" _doc_dest_dir="${_options[2]}"
  local _dir_for_staged_wiki_files="${_wiki_dir}/${_options[3]}"
  local _dir_for_staged_techdocs_files="${_techdocs_dir}/${_options[4]}"

  for _each in "${_subcommands[@]}"; do
    if [[ "${_each}" == "clean" ]]; then
      clean "${_pwd}/${_wiki_dir}" "${_pwd}/${_techdocs_dir}" "${_pwd}/${_doc_dest_dir}"
    elif [[ "${_each}" == "compile-wiki" ]]; then
      compile-docs "${_doc_dest_dir}" "${_mappings[@]}"
      compile-wiki "${_wiki_dir}" "${_doc_dest_dir}" "${_dir_for_staged_wiki_files}"
    elif [[ "${_each}" == "compile-techdocs" ]]; then
      compile-docs "${_doc_dest_dir}" "${_mappings[@]}"
      compile-techdocs "${_techdocs_dir}" "${_doc_dest_dir}" "${_dir_for_staged_techdocs_files}"
    elif [[ "${_each}" == "publish-wiki" ]]; then
      # Deploy the generated github-wiki site.
      publish-wiki "${_wiki_dir}"
    elif [[ "${_each}" == "publish-techdocs" ]]; then
      # Deploy the generated github-wiki site.
      publish-techdocs "${_techdocs_dir}"
    else
      abort "Unknown subcommand: '${_each}' was given."
    fi
  done
}

main "${@}"