#!/bin/bash -ex

create_domain() {
    local AS_PASSWORDFILE_CHANGE=/tmp/passwordfile
    local SAVE_MASTER_PASSWORD;
    echo -e "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORDFILE_CHANGE}"
    if [ "${AS_ADMIN_MASTERPASSWORD}" == "" ] || [ "${AS_ADMIN_MASTERPASSWORD}" == "changeit" ]; then
        SAVE_MASTER_PASSWORD=false
        echo -e "AS_ADMIN_MASTERPASSWORD=changeit" >> ${AS_PASSWORDFILE_CHANGE}
    else
        SAVE_MASTER_PASSWORD=true
        echo -e "AS_ADMIN_MASTERPASSWORD=${AS_ADMIN_MASTERPASSWORD}" >> ${AS_PASSWORDFILE_CHANGE}
    fi
    echo -e "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_ADMIN_PASSWORDFILE}"
    asadmin --passwordfile "${AS_PASSWORDFILE_CHANGE}" create-domain --savemasterpassword ${SAVE_MASTER_PASSWORD} --keytooloptions CN="${AS_HOSTNAME}" "${AS_DOMAIN_NAME}"
    rm -rf "${PATH_GF_DOMAIN}/autodeploy"
    ln -s /deploy "${PATH_GF_DOMAIN}/autodeploy"

    asadmin start-domain ${AS_DOMAIN_NAME}
    local AS_COMMANDFILE=/tmp/commandfile
    echo -e "\
      set-log-attributes org.glassfish.main.jul.handler.GlassFishLogHandler.enabled=false\n
      set-log-attributes org.glassfish.main.jul.handler.SimpleLogHandler.level=FINEST\n
    " > "${AS_COMMANDFILE}"
    if [ "${AS_ADMIN_PASSWORD}" != "" ]; then
        echo -e "enable-secure-admin\n" >> "${AS_COMMANDFILE}"
    fi
    cat "${AS_COMMANDFILE}"
    asadmin --passwordfile=${AS_ADMIN_PASSWORDFILE} multimode --file "${AS_COMMANDFILE}"
    sleep 1
    asadmin --passwordfile=${AS_ADMIN_PASSWORDFILE} stop-domain "${AS_DOMAIN_NAME}"
    rm -f "${PATH_GF_SERVER_LOG}"
}

on_exit () {
    EXIT_CODE=$?
    set +e;
    asadmin stop-domain --echo --force --kill "${AS_DOMAIN_NAME}"
    exit $EXIT_CODE;
}

if [ -d "${PATH_GF_DOMAIN}" ]; then
    FIRST_RUN=false
else
    FIRST_RUN=true
fi

trap on_exit EXIT

if $FIRST_RUN; then
    create_domain
fi

if [ ! "${AS_ADMIN_PASSWORD}" == "" ]; then
    export AS_ADMIN_SECURE=true
fi

unset AS_ADMIN_PASSWORD
unset AS_ADMIN_MASTERPASSWORD
unset AS_ADMIN_USER
env | sort

if [ -f custom/init.sh ]; then
    /bin/bash custom/init.sh
fi

if [ -f custom/init.asadmin ]; then
    asadmin --passwordfile=${AS_ADMIN_PASSWORDFILE} --interactive=false multimode -f custom/init.asadmin
fi


if [ "$1" != 'asadmin' -a "$1" != 'startserv' ]; then
    exec "$@"
fi

if $FIRST_RUN; then
    rm -rf "${PATH_GF_DOMAIN}/autodeploy/.autodeploystatus"
    history -c
fi

echo "STARTING DOMAIN!"
echo "Name: ${AS_DOMAIN_NAME}, $@"
if [ "$1" == 'startserv' ]; then
    exec "$@"
fi

"$@" & wait
