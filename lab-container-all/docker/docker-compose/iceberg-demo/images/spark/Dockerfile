FROM dpp/hadoop

ARG HIVE_VERSION=2.3.7

RUN set -xeu; \
    mkdir /tmp/dl; \
    cd /tmp/dl; \
    wget https://archive.apache.org/dist/hive/hive-${HIVE_VERSION}/apache-hive-${HIVE_VERSION}-bin.tar.gz.sha256; \
    wget --progress=dot:giga -O apache-hive-${HIVE_VERSION}-bin.tar.gz \
        "https://archive.apache.org/dist/hive/hive-${HIVE_VERSION}/apache-hive-${HIVE_VERSION}-bin.tar.gz"; \
    sha256sum -c apache-hive-${HIVE_VERSION}-bin.tar.gz.sha256; \
    tar -xzf apache-hive-${HIVE_VERSION}-bin.tar.gz; \
    mv apache-hive-${HIVE_VERSION}-bin /opt/hive; \
    rm -rf /tmp/dl;

ENV PATH=/opt/hive/bin:${PATH}

# https://issues.apache.org/jira/browse/HIVE-22915
RUN rm /opt/hive/lib/guava-*.jar && cp /opt/hadoop/share/hadoop/hdfs/lib/guava-27.0-jre.jar /opt/hive/lib/


ARG SPARK_VERSION=3.0.1

RUN set -xeu; \
    mkdir /tmp/dl; \
    cd /tmp/dl; \
    wget https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-without-hadoop.tgz.sha512; \
    wget --progress=dot:giga -O spark-${SPARK_VERSION}-bin-without-hadoop.tgz \
        "https://www.apache.org/dyn/mirrors/mirrors.cgi?action=download&filename=spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-without-hadoop.tgz"; \
    gpg --print-md SHA512 spark-${SPARK_VERSION}-bin-without-hadoop.tgz | diff - spark-${SPARK_VERSION}-bin-without-hadoop.tgz.sha512; \
    tar -xzf spark-${SPARK_VERSION}-bin-without-hadoop.tgz; \
    mv spark-${SPARK_VERSION}-bin-without-hadoop /opt/spark; \
    rm -rf /tmp/dl

ENV PATH=/opt/spark/bin:${PATH}


ARG HUDI_VERSION=0.7.0

RUN cd /opt/spark/jars \
 && wget https://repo1.maven.org/maven2/org/apache/spark/spark-hive_2.12/${SPARK_VERSION}/spark-hive_2.12-${SPARK_VERSION}.jar \
 && wget --progress=dot:meta https://repo1.maven.org/maven2/org/apache/hudi/hudi-spark-bundle_2.12/${HUDI_VERSION}/hudi-spark-bundle_2.12-${HUDI_VERSION}.jar \
 && wget https://repo1.maven.org/maven2/org/apache/spark/spark-avro_2.12/${SPARK_VERSION}/spark-avro_2.12-${SPARK_VERSION}.jar

ARG ICEBERG_VERSION=0.11.0

RUN cd /opt/spark/jars \
 && wget https://search.maven.org/remotecontent?filepath=org/apache/iceberg/iceberg-spark3-runtime/${ICEBERG_VERSION}/iceberg-spark3-runtime-${ICEBERG_VERSION}.jar \
    -O iceberg-spark3-runtime-${ICEBERG_VERSION}.jar

RUN set -xeu; \
    echo "export SPARK_DIST_CLASSPATH=\$(hadoop classpath):/opt/hive/lib/hive-exec-2.3.7.jar:/opt/hive/lib/hive-common-2.3.7.jar:/opt/hive/lib/hive-serde-2.3.7.jar" >> /opt/spark/conf/spark-env.sh; \
    echo "export LD_LIBRARY_PATH=\$HADOOP_HOME/lib/native/:\$LD_LIBRARY_PATH" >> /opt/spark/conf/spark-env.sh;
