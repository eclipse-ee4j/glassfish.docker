FROM @embedded.docker.baseImage@

LABEL org.opencontainers.image.base.name="@embedded.docker.baseImage@"
LABEL org.opencontainers.image.source="https://github.com/eclipse-ee4j/glassfish.docker"
LABEL org.opencontainers.image.url="https://github.com/eclipse-ee4j/glassfish.docker/wiki"

LABEL org.opencontainers.image.title="Embedded Eclipse GlassFish"
LABEL org.opencontainers.image.description="The Official Eclipse Docker Image for Embedded GlassFish"
LABEL org.opencontainers.image.version="@glassfish.version@"

LABEL org.opencontainers.image.authors="glassfish-dev@eclipse.org"
LABEL org.opencontainers.image.vendor="Eclipse Foundation"
LABEL org.opencontainers.image.licenses="EPL-2.0"

EXPOSE 8080 8181

# You should use own credentials and own files! These are just defaults.
ARG UID=1000 \
    GID=1000
ENV PATH_GF_HOME=/opt/glassfish
ENV GLASSFISH_DOWNLOAD_SHA512=@embedded.sha512@ \
    GLASSFISH_VERSION=@glassfish.version@ \
    PATH_GF_JAR=${PATH_GF_HOME}/glassfish-embedded-all.jar

RUN true \
    && set -x \
    && apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y gpg unzip \
    && mkdir -p "${PATH_GF_HOME}" \
    && curl -fL "https://repo1.maven.org/maven2/org/glassfish/main/extras/glassfish-embedded-all/${GLASSFISH_VERSION}/glassfish-embedded-all-${GLASSFISH_VERSION}.jar.asc" -o glassfish-embedded-all.jar.asc \
    && curl -fL "https://repo1.maven.org/maven2/org/glassfish/main/extras/glassfish-embedded-all/${GLASSFISH_VERSION}/glassfish-embedded-all-${GLASSFISH_VERSION}.jar" -o "${PATH_GF_HOME}"/glassfish-embedded-all.jar \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --batch --keyserver keyserver.ubuntu.com --recv-keys D4A77129F00F736293BE5A51AFC18A2271EDDFE1 \
    && gpg --batch --verify glassfish-embedded-all.jar.asc "${PATH_GF_HOME}"/glassfish-embedded-all.jar \
    && rm glassfish-embedded-all.jar.asc \
    && echo "$GLASSFISH_DOWNLOAD_SHA512 "${PATH_GF_HOME}"/glassfish-embedded-all.jar" | sha512sum --strict --check \
    && userdel -r ubuntu \
    && groupadd -g ${GID} glassfish \
    && useradd -r -l -u ${UID} -g ${GID} -d "${PATH_GF_HOME}" -s /bin/bash glassfish \
    && env | sort \
    && chown -R glassfish:glassfish "${PATH_GF_HOME}" \
    && mkdir ${PATH_GF_HOME}/autodeploy \
    && ln -s ${PATH_GF_HOME}/autodeploy /deploy \
    && echo "Installation was successful."

USER glassfish
WORKDIR ${PATH_GF_HOME}
ENTRYPOINT ["java", "-jar", "glassfish-embedded-all.jar"]
