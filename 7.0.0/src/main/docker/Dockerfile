FROM eclipse-temurin:17-jdk

EXPOSE 4848 9009 8080 8181 7676 8686 3700 3820 3920 6666

# You should use own credentials and own files! These are just defaults.
ARG AS_ADMIN_PASSWORD=admin \
    PATH_GF_PASSWORD_FILE_FOR_CHANGE=/password-change.txt
ENV PATH_GF_HOME=/opt/glassfish7
ENV AS_USER=admin \
    AS_PASSWORD_FILE=/password.txt \
    AS_TRACE=false \
    AS_TRACE_LOGGING=false \
    AS_TRACE_BOOTSTRAP=false \
    AS_STOP_TIMEOUT=9500 \
    GLASSFISH_DOWNLOAD_SHA512=27d9cef982e5380f3e1d8ba1b4c3e9304258ab37e5bac520c7aa9e5ff08d0d6cb49a689134e250ded189e996948f6847046c2b01b1970b54c22f857dc4bd1ef3 \
    GLASSFISH_VERSION=7.0.0 \
    PATH_GF_BIN=${PATH_GF_HOME}/bin \
    PATH_GF_SERVER_LOG="${PATH_GF_HOME}/glassfish/domains/domain1/logs/server.log"
ENV PATH="${PATH_GF_BIN}:${PATH}"

COPY run-glassfish.sh ${PATH_GF_BIN}/run-glassfish.sh

RUN true \
    && set -x \
    && apt update \
    && apt install -y gpg unzip \
    && curl -fL "https://repo1.maven.org/maven2/org/glassfish/main/distributions/glassfish/$GLASSFISH_VERSION/glassfish-$GLASSFISH_VERSION.zip.asc" -o glassfish.zip.asc \
    && curl -fL "https://repo1.maven.org/maven2/org/glassfish/main/distributions/glassfish/$GLASSFISH_VERSION/glassfish-$GLASSFISH_VERSION.zip" -o glassfish.zip \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --batch --keyserver keyserver.ubuntu.com --recv-keys D4A77129F00F736293BE5A51AFC18A2271EDDFE1 \
    && gpg --batch --verify glassfish.zip.asc glassfish.zip \
    && rm -r "$GNUPGHOME" glassfish.zip.asc \
    && echo "$GLASSFISH_DOWNLOAD_SHA512 glassfish.zip" | sha512sum --strict --check \
    && mkdir -p "${PATH_GF_HOME}" \
    && unzip -q glassfish.zip -d "${PATH_GF_HOME}/.." \
    && rm glassfish.zip \
    && useradd -r -l -u 1000 -d "${PATH_GF_HOME}" -s /bin/bash glassfish \
    && echo "Generating password file at ${AS_PASSWORD_FILE} ..." \
    && set +x \
    && echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}" \
    && echo "AS_ADMIN_PASSWORD=" > "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && echo "AS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && echo "" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && unset AS_ADMIN_PASSWORD \
    && set -x \
    && env | sort \
    && asadmin start-domain \
    && asadmin --user ${AS_USER} --passwordfile ${PATH_GF_PASSWORD_FILE_FOR_CHANGE} change-admin-password \
    && asadmin stop-domain --kill \
    && asadmin start-domain \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} set-log-attributes org.glassfish.main.jul.handler.GlassFishLogHandler.enabled=false \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} set-log-attributes org.glassfish.main.jul.handler.SimpleLogHandler.level=FINEST \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} enable-secure-admin \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} stop-domain \
    && rm -f ${PATH_GF_SERVER_LOG} ${PATH_GF_PASSWORD_FILE_FOR_CHANGE} \
    && chown -R glassfish:glassfish "${PATH_GF_HOME}" \
    && chmod +x ${PATH_GF_BIN}/run-glassfish.sh \
    && ls -la ${PATH_GF_BIN} \
    && echo "Installation was successful."
USER glassfish
WORKDIR ${PATH_GF_HOME}
ENTRYPOINT ["run-glassfish.sh"]
CMD ["asadmin", "start-domain", "--verbose"]
