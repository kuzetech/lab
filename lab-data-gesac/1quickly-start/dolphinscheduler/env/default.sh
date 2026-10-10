if [ "${DOLPHINSCHEDULER_DEFAULT_ENV_LOADED:-}" = "1" ]; then
  return 0 2>/dev/null || exit 0
fi

export DOLPHINSCHEDULER_DEFAULT_ENV_LOADED=1
export HADOOP_HOME=/opt/hadoop-3.2.1
export HADOOP_CONF_DIR=/etc/hadoop
export YARN_CONF_DIR=/etc/hadoop
export FLINK_HOME=/opt/flink
export PATH=$FLINK_HOME/bin:$HADOOP_HOME/bin:$PATH
export HADOOP_MAPRED_HOME="${HADOOP_MAPRED_HOME:-${HADOOP_HOME}}"

if command -v hadoop >/dev/null 2>&1; then
  export HADOOP_CLASSPATH="${HADOOP_CLASSPATH:-$(hadoop classpath 2>/dev/null)}"
fi
