FROM @gf.docker.baseImage@

LABEL org.opencontainers.image.base.name="@gf.docker.baseImage@"
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
    AS_ADMIN_MASTERPASSWORD=changeit \
    UID=1000 \
    GID=1000
ENV PATH_GF_HOME="/opt/gfinstall" \
    AS_HOSTNAME="localhost" \
    AS_ADMIN_HOST="localhost" \
    AS_NO_REVERSE_DNS="true" \
    AS_ADMIN_PORT=4848 \
    AS_ADMIN_USER=admin \
    AS_DOMAIN_NAME=domain1
ENV AS_ADMIN_PASSWORDFILE="${PATH_GF_HOME}/password.txt" \
    AS_TRACE=false \
    AS_TRACE_LOGGING=false \
    AS_TRACE_BOOTSTRAP=false \
    AS_STOP_TIMEOUT=9500 \
    GLASSFISH_DOWNLOAD_SHA512=@server.sha512@ \
    GLASSFISH_VERSION=@glassfish.version@ \
    PATH_GF_BIN="${PATH_GF_HOME}/bin" \
    PATH_GF_DOMAIN="${PATH_GF_HOME}/glassfish/domains/${AS_DOMAIN_NAME}"
ENV PATH_GF_SERVER_LOG="${PATH_GF_DOMAIN}/logs/server.log" \
    PATH="${PATH_GF_BIN}:${PATH}"

RUN true \
    && set -x \
    && TMPDIR=$(mktemp -d /opt/gfXXXX) \
    && cd "$TMPDIR" \
    && curl -fL "@gf.docker.mavenUrl@/org/glassfish/main/distributions/glassfish/${GLASSFISH_VERSION}/glassfish-@gf.docker.server.versionId@.zip" -o glassfish.zip \
    && curl -fL "@gf.docker.mavenUrl@/org/glassfish/main/distributions/glassfish/${GLASSFISH_VERSION}/glassfish-@gf.docker.server.versionId@.zip.asc" -o glassfish.zip.asc \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --batch --keyserver keyserver.ubuntu.com --recv-keys D4A77129F00F736293BE5A51AFC18A2271EDDFE1 \
    && gpg --batch --verify glassfish.zip.asc glassfish.zip \
    && rm glassfish.zip.asc \
    && echo "$GLASSFISH_DOWNLOAD_SHA512 glassfish.zip" | sha512sum --strict --check \
    && jar -xf glassfish.zip \
    && cd \
    && mkdir -p "${PATH_GF_HOME}" \
    && cp -r "$TMPDIR"/*/* "${PATH_GF_HOME}/" \
    && rm -rf "$TMPDIR" \
    && rm -rf "${PATH_GF_DOMAIN}" \
    && userdel -r ubuntu \
    && groupadd -g ${GID} glassfish \
    && useradd -r -l -u ${UID} -g ${GID} -d "${PATH_GF_HOME}" -s /bin/bash glassfish \
    && mkdir -p "/deploy/bundles" \
    && chown -R glassfish:glassfish "/deploy" \
    && chown -R glassfish:glassfish "${PATH_GF_HOME}" \
    && chmod u+x $(find "${PATH_GF_HOME}" -type f -name "asadmin" -o -name "nadmin" -o -name "startserv" -o -name "*.sh") \
    && chmod -R go-rwx "${PATH_GF_HOME}" \
    && echo "Installation was successful."

COPY --chmod=700 --chown=glassfish docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

USER glassfish
WORKDIR ${PATH_GF_HOME}
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["startserv"]
