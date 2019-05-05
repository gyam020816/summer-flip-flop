#!/bin/sh

SCRIPT__MIRROR_LOCATION=$(cat .gitlab-to-github-mirror-statuses)
curl \
  -d "{\"title\":\"💎 $CI_COMMIT_REF_NAME (auto)\", \"head\":\"$CI_COMMIT_REF_NAME\", \"base\":\"master\", \"body\":\"💎 Auto-generated PR for branch $CI_COMMIT_REF_NAME\",\"draft\":true}" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/vnd.github.shadow-cat-preview' \
  -u Hurricaaane:$GITHUB_STATUS_TOKEN \
  https://api.github.com/repos/$SCRIPT__MIRROR_LOCATION/pulls || true
