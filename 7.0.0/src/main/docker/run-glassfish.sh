#!/bin/bash
set -e;

if [ "$1" != 'asadmin' ]; then
    "$@"
    exit 0;
fi

on_exit () {
    EXIT_CODE=$?
    set +e;
    ps -lAf;
    asadmin stop-domain --force --kill;
    exit $EXIT_CODE;
}
trap on_exit EXIT

env|sort && "$@"
