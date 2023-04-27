#!/bin/bash
set -e;

if [ "$1" != 'asadmin' ]; then
    exec "$@"
fi

on_exit () {
    EXIT_CODE=$?
    set +e;
    ps -lAf;
    asadmin stop-domain --force --kill;
    exit $EXIT_CODE;
}
trap on_exit EXIT

CONTAINER_ALREADY_STARTED="CONTAINER_ALREADY_STARTED_PLACEHOLDER"
if [ ! -f "$CONTAINER_ALREADY_STARTED" ]
then
    touch "$CONTAINER_ALREADY_STARTED" &&
    rm -rf glassfish/domains/domain1/autodeploy/.autodeploystatus || true
fi

env|sort && "$@" & wait
