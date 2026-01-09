FROM @docker.baseImage@

LABEL org.opencontainers.image.base.name="@docker.baseImage@"
LABEL org.opencontainers.image.source="https://github.com/eclipse-ee4j/glassfish.docker"
LABEL org.opencontainers.image.url="https://github.com/eclipse-ee4j/glassfish.docker/wiki"

LABEL org.opencontainers.image.title="Eclipse GlassFish"
LABEL org.opencontainers.image.description="The Official Eclipse GlassFish Docker Image"
LABEL org.opencontainers.image.version="@glassfish.version@"

LABEL org.opencontainers.image.authors="glassfish-dev@eclipse.org"
LABEL org.opencontainers.image.vendor="Eclipse Foundation"
LABEL org.opencontainers.image.licenses="EPL-2.0"

EXPOSE 4848 9009 8080 8181 8686 3700 3820 3920 6666

# You should use own credentials and own files! These are just defaults.
ARG AS_ADMIN_PASSWORD=admin \
    PATH_GF_PASSWORD_FILE_FOR_CHANGE=/password-change.txt \
    UID=1000 \
    GID=1000
ENV PATH_GF_HOME=/opt/glassfish7 \
    AS_ADMIN_USER=admin \
    AS_ADMIN_PASSWORDFILE=/password.txt
ENV AS_USER=${AS_ADMIN_USER} \
    AS_PASSWORD_FILE=${AS_ADMIN_PASSWORDFILE} \
    AS_TRACE=false \
    AS_TRACE_LOGGING=false \
    AS_TRACE_BOOTSTRAP=false \
    AS_STOP_TIMEOUT=9500 \
    GLASSFISH_DOWNLOAD_SHA512=@glassfish.zip.sha512@ \
    GLASSFISH_VERSION=@glassfish.version@ \
    PATH_GF_BIN=${PATH_GF_HOME}/bin \
    PATH_GF_SERVER_LOG="${PATH_GF_HOME}/glassfish/domains/domain1/logs/server.log"
ENV PATH="${PATH_GF_BIN}:${PATH}"

RUN true \
    && set -x \
    && apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y gpg unzip \
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
    && userdel -r ubuntu \
    && groupadd -g ${GID} glassfish \
    && useradd -r -l -u ${UID} -g ${GID} -d "${PATH_GF_HOME}" -s /bin/bash glassfish \
    && echo "Generating password file at ${AS_PASSWORD_FILE} ..." \
    && set +x \
    && echo "AS_ADMIN_PASSWORD=${AS_ADMIN_PASSWORD}" > "${AS_PASSWORD_FILE}" \
    && chown glassfish:glassfish "${AS_PASSWORD_FILE}" \
    && echo "AS_ADMIN_PASSWORD=" > "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && echo "AS_ADMIN_NEWPASSWORD=${AS_ADMIN_PASSWORD}" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && echo "" >> "${PATH_GF_PASSWORD_FILE_FOR_CHANGE}" \
    && unset AS_ADMIN_PASSWORD \
    && set -x \
    && env | sort \
    && AS_START_TIMEOUT=120000 asadmin start-domain \
    && curl  -o /dev/null http://localhost:4848 \
    && asadmin --passwordfile ${PATH_GF_PASSWORD_FILE_FOR_CHANGE} change-admin-password \
    && asadmin stop-domain --kill \
    && AS_START_TIMEOUT=120000 asadmin start-domain \
    && curl  -o /dev/null http://localhost:4848 \
    && asadmin set-log-attributes org.glassfish.main.jul.handler.GlassFishLogHandler.enabled=false \
    && asadmin set-log-attributes org.glassfish.main.jul.handler.SimpleLogHandler.level=FINEST \
    && asadmin enable-secure-admin \
    && asadmin stop-domain --kill \
    && rm -f ${PATH_GF_SERVER_LOG} ${PATH_GF_PASSWORD_FILE_FOR_CHANGE} \
    && chown -R glassfish:glassfish "${PATH_GF_HOME}" \
    && mkdir ${PATH_GF_HOME}/autodeploy \
    && echo "Installation was successful."

COPY --chmod=755 docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

USER glassfish
WORKDIR ${PATH_GF_HOME}
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["startserv"]
