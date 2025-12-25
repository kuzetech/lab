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
import com.kuzetech.bigdata.flink.source.WaterMarkerSingleParallelSource;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class ProducerWaterMarkerJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = FlinkUtil.initEnv(parameterTool);

        final JobConfig jobConfig = new JobConfig(parameterTool);

        SingleOutputStreamOperator<Long> sourceStream = env.addSource(new WaterMarkerSingleParallelSource())
                .setParallelism(1)
                .uid("source")
                .name("source");

        KafkaSinkBuilder<String> sinkBuilder = KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("test")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build());

        sourceStream
                .map(Object::toString)
                .sinkTo(sinkBuilder.build())
                .uid("sink")
                .name("sink");

        env.execute(jobConfig.getJobName());
    }
}
