export HADOOP_HOME=/opt/hadoop-3.2.1
export HADOOP_CONF_DIR=/etc/hadoop
export YARN_CONF_DIR=/etc/hadoop
export FLINK_HOME=/opt/flink
export PATH=$FLINK_HOME/bin:$HADOOP_HOME/bin:$PATH
export HADOOP_CLASSPATH=$(hadoop classpath)