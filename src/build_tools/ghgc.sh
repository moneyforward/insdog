#!/bin/bash

#####
## A small script to clean up "artifacts" of GitHub actions.
## This script removes all the artifacts of autotest-ca except for the most recent 10 items.
#####

OWNER=moneyforward
REPO="autotest-ca"
TOKEN="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"

function compose_github_artifacts_url() {
  local _id="${1}"
  echo "https://api.github.com/repos/${OWNER}/${REPO}/actions/artifacts/${_id}"
}

function list_artifacts() {
    curl -H "Authorization: token ${TOKEN}" \
	 -H "Accept: application/vnd.github.v3+json" \
	 "https://api.github.com/repos/${OWNER}/${REPO}/actions/artifacts" | \
	jq '.artifacts |sort_by(.created_at)|reverse|.[10:]|.[]| {id, name, expired, created_at}|.id'
}

function main() {
  for i in $(list_artifacts); do
    curl -X DELETE \
     -H "Authorization: token ${TOKEN}" \
     -H "Accept: application/vnd.github.v3+json" \
     "$(compose_github_artifacts_url "${i}")"
  done
}

main "${@}"

