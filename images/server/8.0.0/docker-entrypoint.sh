#!/bin/bash
set -e

change_passwords () {
    local PATH_GF_PASSWORD_FILE_FOR_CHANGE=/tmp/passwordfile
    local COMMAND=""
    rm -f "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}"

    if [ "${AS_ADMIN_PASSWORD}" != "" ] && [ "${AS_ADMIN_PASSWORD}" != "admin" ]; then
        echo -e "AS_ADMIN_PASSWORD=admin" > "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}"
        echo -e "AS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}"
        COMMAND="change-admin-password"
    fi

    if [ "${AS_ADMIN_MASTERPASSWORD}" != "" ] && [ "${AS_ADMIN_MASTERPASSWORD}" != "changeit" ]; then
        echo -e "AS_ADMIN_MASTERPASSWORD=changeit" >> ${PATH_GF_PASSWORD_FILE_FOR_CHANGE}
        echo -e "AS_ADMIN_NEWMASTERPASSWORD=${AS_ADMIN_MASTERPASSWORD}" >> ${PATH_GF_PASSWORD_FILE_FOR_CHANGE}
        COMMAND+="\nchange-master-password --savemasterpassword=true"
    fi

    if [ "${COMMAND}" != "" ]; then
        echo -e "${COMMAND}" | asadmin --interactive=false --passwordfile=${PATH_GF_PASSWORD_FILE_FOR_CHANGE}
        echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}"
    fi

    rm -f ${PATH_GF_PASSWORD_FILE_FOR_CHANGE}
    unset AS_ADMIN_PASSWORD;
    unset AS_ADMIN_MASTERPASSWORD;
    history -c
}

on_exit () {
    EXIT_CODE=$?
    set +e;
    ps -lAf;
    asadmin stop-domain --force --kill;
    exit $EXIT_CODE;
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
    touch "$CONTAINER_ALREADY_STARTED" && rm -rf glassfish/domains/domain1/autodeploy/.autodeploystatus
fi

if [ "${AS_TRACE}" == true ]; then
    env | sort
fi

if [ "$1" == 'startserv' ]; then
    exec "$@"
fi

trap on_exit EXIT

"$@" & wait
