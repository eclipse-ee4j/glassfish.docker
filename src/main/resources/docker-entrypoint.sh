#!/bin/bash
set -e;

change_passwords () {
  local PWD_FILE=/tmp/passwordfile
  if [ x${AS_ADMIN_PASSWORD} != x ]; then
    echo -e "AS_ADMIN_PASSWORD=admin\nAS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" > $PWD_FILE
    asadmin change-admin-password --passwordfile=${PWD_FILE}
    rm -rf ${PWD_FILE}
    echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}"
  fi
  if [ x${AS_ADMIN_MASTERPASSWORD} != x ]; then
    echo -e "AS_ADMIN_MASTERPASSWORD=changeit\nAS_ADMIN_NEWMASTERPASSWORD=${AS_ADMIN_MASTERPASSWORD}" > ${PWD_FILE}
    asadmin change-master-password --passwordfile=${PWD_FILE} --savemasterpassword=true
    rm -rf ${PWD_FILE}
  fi
}

change_passwords

if [ -f custom/init.sh ]; then
  /bin/bash custom/init.sh
fi

if [ -f custom/init.asadmin ]; then
  asadmin --interactive=false multimode -f custom/init.asadmin
fi


if [ "$1" != 'asadmin' -a "$1" != 'startserv' -a "$1" != 'runembedded' ]; then
    exec "$@"
fi

if [ "$1" == 'runembedded' ]; then
  shift 1
  if [[ "$SUSPEND" == true ]]
    then 
      JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=9009 $JVM_OPTS"
  elif [[ "$DEBUG" == true ]]
    then
      JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9009 $JVM_OPTS"
  fi
  exec java $JVM_OPTS -jar glassfish/lib/embedded/glassfish-embedded-static-shell.jar "$@"
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
