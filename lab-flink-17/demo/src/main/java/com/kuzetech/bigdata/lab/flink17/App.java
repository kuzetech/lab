package com.kuzetech.bigdata.lab.flink17;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.datagen.DataGeneratorSource;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

public class App {
    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataGeneratorSource<String> source = new DataGeneratorSource<>(
                RandomGenerator.stringGenerator(10),
                1,
                null);

        env.addSource(source).returns(Types.STRING).print();

        env.execute("test");
    }
}
