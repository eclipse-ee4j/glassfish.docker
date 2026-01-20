#!/bin/bash
set -e

change_passwords () {
  local PWD_FILE=/tmp/passwordfile
  local COMMAND=
  rm -rf $PWD_FILE

  if [ "${AS_ADMIN_PASSWORD}" != "" ] && [ "${AS_ADMIN_PASSWORD}" != "admin" ]; then
    echo -e "AS_ADMIN_PASSWORD=admin\nAS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" >> $PWD_FILE
    COMMAND="change-admin-password"
    echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}"
  fi

  if [ "${AS_ADMIN_MASTERPASSWORD}" != "" ] && [ "${AS_ADMIN_MASTERPASSWORD}" != "changeit" ]; then
    echo -e "AS_ADMIN_MASTERPASSWORD=changeit\nAS_ADMIN_NEWMASTERPASSWORD=${AS_ADMIN_MASTERPASSWORD}" >> ${PWD_FILE}
    COMMAND="${COMMAND}\nchange-master-password --savemasterpassword=true"
  fi

  if [ "${COMMAND}" != "" ]; then
    echo -e "${COMMAND}" | asadmin --interactive=false --passwordfile=${PWD_FILE} 
  fi

  rm -rf ${PWD_FILE}
  history -c
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

if [ "${AS_TRACE}" == true ]; then
    env|sort
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

"$@" & wait
