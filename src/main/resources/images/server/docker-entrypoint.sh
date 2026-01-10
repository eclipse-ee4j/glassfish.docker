#!/bin/bash
set -e;

change_passwords () {
  local PWD_FILE=/tmp/passwordfile
  local COMMAND=
  rm -rf $PWD_FILE

  if [ x"${AS_ADMIN_PASSWORD}" != x ]; then
    echo -e "AS_ADMIN_PASSWORD=admin\nAS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" >> $PWD_FILE
    COMMAND="change-admin-password --passwordfile=${PWD_FILE}"
    echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}"
  fi

  if [ x"${AS_ADMIN_MASTERPASSWORD}" != x ]; then
    echo -e "AS_ADMIN_MASTERPASSWORD=changeit\nAS_ADMIN_NEWMASTERPASSWORD=${AS_ADMIN_MASTERPASSWORD}" >> ${PWD_FILE}
    COMMAND="${COMMAND}
change-master-password --passwordfile=${PWD_FILE} --savemasterpassword=true"
  fi

  if [ x"${COMMAND}" != x ]; then
    printf "${COMMAND}" > /tmp/commands
    asadmin multimode --interactive=false --file /tmp/commands
    rm -rf /tmp/commands
  fi

  rm -rf ${PWD_FILE}
}

change_passwords

if [ -f custom/init.sh ]; then
  /bin/bash custom/init.sh
fi

if [ -f custom/init.asadmin ]; then
  asadmin --interactive=false multimode -f custom/init.asadmin
fi


if [ "$1" != 'asadmin' -a "$1" != 'startserv' ]; then
    exec "$@"
fi

CONTAINER_ALREADY_STARTED="CONTAINER_ALREADY_STARTED_PLACEHOLDER"
if [ ! -f "$CONTAINER_ALREADY_STARTED" ]
then
    touch "$CONTAINER_ALREADY_STARTED" &&
    rm -rf glassfish/domains/domain1/autodeploy/.autodeploystatus || true
fi

if [ "$1" == 'startserv' ]; then
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

env|sort && "$@" & wait
