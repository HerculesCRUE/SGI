#!/usr/bin/env sh
set -euvx

#copy capp file to deployment after volume creation
cp ${WORKING_DIRECTORY}/base-capp/*.car ${WSO2_SERVER_HOME}/repository/deployment/server/carbonapps/

# continue with normal execution
/bin/sh ${WORKING_DIRECTORY}/docker-entrypoint.sh "$@"