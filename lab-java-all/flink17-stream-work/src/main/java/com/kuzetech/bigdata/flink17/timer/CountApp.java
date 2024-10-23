package com.kuzetech.bigdata.flink17.timer;

import com.kuzetech.bigdata.flink17.udsource.KeyAndTimeEventParallelSource;
import com.kuzetech.bigdata.flink17.udsource.model.KeyAndTimeEvent;
import com.kuzetech.bigdata.flink17.utils.FlinkUtil;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CountApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment();

        DataStreamSource<KeyAndTimeEvent> sourceStream = env.addSource(new KeyAndTimeEventParallelSource());

        SingleOutputStreamOperator<CountWithTimestamp> processStream = sourceStream
                .keyBy(KeyAndTimeEvent::getKey)
                .process(new CountTimerProcessor());

        processStream.print();

        env.execute("CountApp");
    }
}
