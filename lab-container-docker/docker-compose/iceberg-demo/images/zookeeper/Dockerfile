FROM zookeeper:3.6.2

# Remove static heap flags (Xmx), we want to use -XX:+UseContainerSupport
RUN set -xe;\
    cd $(dirname $(which zkServer.sh));\
    sed -i -E 's/(export SERVER_JVMFLAGS.*)/# \1/g' zkEnv.sh;\
    sed -i -E 's/(export CLIENT_JVMFLAGS.*)/# \1/g' zkEnv.sh;
