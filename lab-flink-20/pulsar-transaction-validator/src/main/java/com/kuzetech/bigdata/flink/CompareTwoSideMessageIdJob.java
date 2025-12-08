/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.base.JobConfig;
import com.kuzetech.bigdata.flink.func.DiffKeyedProcessFunction;
import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import com.kuzetech.bigdata.flink.kafka.KafkaSourceConfig;
import com.kuzetech.bigdata.flink.kafka.KafkaUtil;
import com.kuzetech.bigdata.flink.kafka.serialization.KafkaFunnyMessageDeserializationSchema;
import com.kuzetech.bigdata.flink.pulsar.PulsarSourceConfig;
import com.kuzetech.bigdata.flink.pulsar.PulsarUtil;
import com.kuzetech.bigdata.flink.pulsar.serialization.PulsarFunnyMessageDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.pulsar.source.PulsarSourceBuilder;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

public class CompareTwoSideMessageIdJob {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = FlinkUtil.initEnv(parameterTool);

        final JobConfig jobConfig = new JobConfig(parameterTool);
        final PulsarSourceConfig pulsarSourceConfig = jobConfig.getPulsarSourceConfig();
        final KafkaSourceConfig kafkaSourceConfig = jobConfig.getKafkaSourceConfig();

        PulsarSourceBuilder<FunnyMessage> pulsarSourceBuilder = PulsarUtil.buildSourceBaseBuilder(pulsarSourceConfig, new PulsarFunnyMessageDeserializationSchema());
        KafkaSourceBuilder<FunnyMessage> kafkaSourceBuilder = KafkaUtil.buildSourceBaseBuilder(kafkaSourceConfig, new KafkaFunnyMessageDeserializationSchema());

        WatermarkStrategy<FunnyMessage> pulsarMsgWatermarkStrategy = WatermarkStrategy
                .<FunnyMessage>forBoundedOutOfOrderness(Duration.ofSeconds(jobConfig.getJobOutOfOrdernessSecond()))
                .withTimestampAssigner((record, timestamp) -> record.getIngestTime())
                .withIdleness(Duration.ofSeconds(60));

        WatermarkStrategy<FunnyMessage> kafkaMsgWatermarkStrategy = WatermarkStrategy
                .<FunnyMessage>forBoundedOutOfOrderness(Duration.ofSeconds(jobConfig.getJobOutOfOrdernessSecond()))
                .withTimestampAssigner((record, timestamp) -> record.getIngestTime())
                .withIdleness(Duration.ofSeconds(60));

        SingleOutputStreamOperator<FunnyMessage> pulsarSourceStream =
                env.fromSource(pulsarSourceBuilder.build(), pulsarMsgWatermarkStrategy, "source-pulsar")
                        .uid("source-pulsar")
                        .name("source-pulsar");

        SingleOutputStreamOperator<FunnyMessage> kafkaSourceStream =
                env.fromSource(kafkaSourceBuilder.build(), kafkaMsgWatermarkStrategy, "source-kafka")
                        .uid("source-kafka")
                        .name("source-kafka");

        pulsarSourceStream.union(kafkaSourceStream)
                .keyBy(FunnyMessage::getDistinctKey)
                .process(new DiffKeyedProcessFunction())
                .uid("statistician")
                .name("statistician")
                .print();

        env.execute(jobConfig.getJobName());
    }
}
