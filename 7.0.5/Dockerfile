FROM eclipse-temurin:17.0.7_7-jdk

EXPOSE 4848 9009 8080 8181 7676 8686 3700 3820 3920 6666

# You should use own credentials and own files! These are just defaults.
ARG AS_ADMIN_PASSWORD=admin \
    PATH_GF_PASSWORD_FILE_FOR_CHANGE=/password-change.txt \
    UID=1000 \
    GID=1000
ENV PATH_GF_HOME=/opt/glassfish7
ENV AS_USER=admin \
    AS_PASSWORD_FILE=/password.txt \
    AS_TRACE=false \
    AS_TRACE_LOGGING=false \
    AS_TRACE_BOOTSTRAP=false \
    AS_STOP_TIMEOUT=9500 \
    GLASSFISH_DOWNLOAD_SHA512=daa519850cf4f81338f1545cc9149861e931612672a04e18a04e31895edc4d7eec55a11bf1f95fa764a45b3c7f0705bdb0cca2c934b1e400d8b9ad8f29fe4df7 \
    GLASSFISH_VERSION=7.0.5 \
    PATH_GF_BIN=${PATH_GF_HOME}/bin \
    PATH_GF_SERVER_LOG="${PATH_GF_HOME}/glassfish/domains/domain1/logs/server.log"
ENV PATH="${PATH_GF_BIN}:${PATH}"

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN true \
    && set -x \
    && apt update \
    && apt install -y gpg unzip \
    && curl -fL "https://repo1.maven.org/maven2/org/glassfish/main/distributions/glassfish/${GLASSFISH_VERSION}/glassfish-${GLASSFISH_VERSION}.zip.asc" -o glassfish.zip.asc \
    && curl -fL "https://repo1.maven.org/maven2/org/glassfish/main/distributions/glassfish/${GLASSFISH_VERSION}/glassfish-${GLASSFISH_VERSION}.zip" -o glassfish.zip \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --batch --keyserver keyserver.ubuntu.com --recv-keys D4A77129F00F736293BE5A51AFC18A2271EDDFE1 \
    && gpg --batch --verify glassfish.zip.asc glassfish.zip \
    && rm glassfish.zip.asc \
    && echo "$GLASSFISH_DOWNLOAD_SHA512 glassfish.zip" | sha512sum --strict --check \
    && mkdir -p "${PATH_GF_HOME}" \
    && unzip -q glassfish.zip -d "${PATH_GF_HOME}/.." \
    && rm glassfish.zip \
    && groupadd -g ${GID} glassfish \
    && useradd -r -l -u ${UID} -g ${GID} -d "${PATH_GF_HOME}" -s /bin/bash glassfish \
    && echo "Generating password file at ${AS_PASSWORD_FILE} ..." \
    && set +x \
    && echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}" \
    && echo "AS_ADMIN_PASSWORD=" > "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && echo "AS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && echo "" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && unset AS_ADMIN_PASSWORD \
    && set -x \
    && env | sort \
    && AS_START_TIMEOUT=120000 asadmin start-domain \
    && curl  -o /dev/null http://localhost:4848 \
    && asadmin --user ${AS_USER} --passwordfile ${PATH_GF_PASSWORD_FILE_FOR_CHANGE} change-admin-password \
    && asadmin stop-domain --kill \
    && AS_START_TIMEOUT=120000 asadmin start-domain \
    && curl  -o /dev/null http://localhost:4848 \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} set-log-attributes org.glassfish.main.jul.handler.GlassFishLogHandler.enabled=false \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} set-log-attributes org.glassfish.main.jul.handler.SimpleLogHandler.level=FINEST \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} enable-secure-admin \
    && asadmin --user ${AS_USER} --passwordfile ${AS_PASSWORD_FILE} stop-domain --kill \
    && rm -f ${PATH_GF_SERVER_LOG} ${PATH_GF_PASSWORD_FILE_FOR_CHANGE} \
    && chown -R glassfish:glassfish "${PATH_GF_HOME}" \
    && chmod +x /usr/local/bin/docker-entrypoint.sh \
    && echo "Installation was successful."
USER glassfish
WORKDIR ${PATH_GF_HOME}
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["asadmin", "start-domain", "--verbose"]
