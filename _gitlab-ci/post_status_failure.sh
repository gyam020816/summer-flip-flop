#!/bin/sh

SCRIPT__MIRROR_LOCATION=$(cat .gitlab-to-github-mirror-statuses)
SCRIPT__STATE=failure
SCRIPT__DESCRIPTION="Pipeline failure"
curl -d "{\"state\":\"$SCRIPT__STATE\", \"context\":\"gitlab-ci\", \"description\":\"$SCRIPT__DESCRIPTION\",\"target_url\":\"$CI_PROJECT_URL/pipelines/$CI_PIPELINE_ID\"}" -H 'Content-Type: application/json' -u Hurricaaane:$GITHUB_STATUS_TOKEN https://api.github.com/repos/$SCRIPT__MIRROR_LOCATION/statuses/$CI_BUILD_REF
