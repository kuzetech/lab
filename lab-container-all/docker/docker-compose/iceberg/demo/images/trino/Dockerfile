FROM openjdk:11.0.8

ARG TRINO_VERSION=352

RUN set -xeu; \
    mkdir /tmp/dl; \
    cd /tmp/dl; \
    wget https://repo1.maven.org/maven2/io/trino/trino-server/${TRINO_VERSION}/trino-server-${TRINO_VERSION}.tar.gz.sha1; \
    echo " trino-server-${TRINO_VERSION}.tar.gz" >> trino-server-${TRINO_VERSION}.tar.gz.sha1; \
    wget --progress=dot:giga https://repo1.maven.org/maven2/io/trino/trino-server/${TRINO_VERSION}/trino-server-${TRINO_VERSION}.tar.gz; \
    sha1sum -c trino-server-${TRINO_VERSION}.tar.gz.sha1; \
    tar -xzf trino-server-${TRINO_VERSION}.tar.gz; \
    mv trino-server-${TRINO_VERSION} /opt/trino; \
    rm -rf /tmp/dl;


RUN set -xeu; \
    apt-get update; \
    apt-get install -y less

RUN set -xeu; \
    mkdir /tmp/dl; \
    cd /tmp/dl; \
    wget https://repo1.maven.org/maven2/io/trino/trino-cli/${TRINO_VERSION}/trino-cli-${TRINO_VERSION}-executable.jar.sha1; \
    echo " trino-cli-${TRINO_VERSION}-executable.jar" >> trino-cli-${TRINO_VERSION}-executable.jar.sha1; \
    wget --progress=dot:mega https://repo1.maven.org/maven2/io/trino/trino-cli/${TRINO_VERSION}/trino-cli-${TRINO_VERSION}-executable.jar; \
    sha1sum -c trino-cli-${TRINO_VERSION}-executable.jar.sha1; \
    mv trino-cli-${TRINO_VERSION}-executable.jar /usr/bin/trino; \
    chmod +x /usr/bin/trino; \
    ln -s /usr/bin/trino /usr/bin/presto; \
    rm -rf /tmp/dl;
