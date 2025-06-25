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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.fake.FakeUser;
import com.kuzetech.bigdata.flink.json.ObjectMapperInstance;
import com.kuzetech.bigdata.flink.kafka.KafkaConfig;
import com.kuzetech.bigdata.flink.kafka.KafkaSourceMessage;
import com.kuzetech.bigdata.flink.kafka.KafkaUtil;
import com.kuzetech.bigdata.flink.source.FakeUserParallelSource;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KafkaProducerJob {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperInstance.getInstance();

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = FlinkUtil.initEnv(parameterTool);

        final KafkaConfig kafkaConfig = KafkaConfig.generateFromParameterTool(parameterTool);

        SingleOutputStreamOperator<FakeUser> sourceStream = env.addSource(new FakeUserParallelSource())
                .uid("source")
                .name("source");

        SingleOutputStreamOperator<KafkaSourceMessage> msgStream = sourceStream.map(u -> new KafkaSourceMessage(OBJECT_MAPPER.writeValueAsBytes(u)));

        KafkaSinkBuilder<KafkaSourceMessage> sinkBuilder = KafkaUtil.buildSinkBaseBuilder(kafkaConfig);

        msgStream.sinkTo(sinkBuilder.build())
                .uid("sink")
                .name("sink");

        env.execute(kafkaConfig.getJobName());
    }
}
