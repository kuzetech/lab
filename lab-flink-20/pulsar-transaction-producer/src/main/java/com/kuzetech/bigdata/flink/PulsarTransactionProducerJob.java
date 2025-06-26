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

import com.kuzetech.bigdata.flink.base.CommonSourceMessage;
import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.kafka.KafkaCommonSourceMessageSerializationSchema;
import com.kuzetech.bigdata.flink.kafka.KafkaConfig;
import com.kuzetech.bigdata.flink.pulsar.PulsarCommonSourceMessageSerializationSchema;
import com.kuzetech.bigdata.flink.pulsar.PulsarConfig;
import com.kuzetech.bigdata.flink.source.FakeFunnyMessageParallelSource;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.connector.pulsar.sink.PulsarSinkBuilder;
import org.apache.flink.connector.pulsar.sink.PulsarSinkOptions;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.IsolationLevel;

import java.util.Locale;
import java.util.Properties;

import static com.kuzetech.bigdata.flink.kafka.KafkaUtil.DEFAULT_PRODUCER_CONFIG;
import static com.kuzetech.bigdata.flink.pulsar.PulsarUtil.DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class PulsarTransactionProducerJob {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = FlinkUtil.initEnv(parameterTool);

        final PulsarConfig pulsarConfig = PulsarConfig.generateFromParameterTool(parameterTool);
        final KafkaConfig kafkaConfig = KafkaConfig.generateFromParameterTool(parameterTool);

        SingleOutputStreamOperator<CommonSourceMessage> sourceStream = env.addSource(new FakeFunnyMessageParallelSource())
                .uid("source")
                .name("source");

        PulsarSinkBuilder<CommonSourceMessage> pulsarSinkBuilder = PulsarSink.builder()
                .setConfig(PulsarSinkOptions.PULSAR_TOPIC_METADATA_REFRESH_INTERVAL, DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS)
                .setConfig(PulsarSinkOptions.PULSAR_ENABLE_SINK_METRICS, true)
                .setServiceUrl(pulsarConfig.getServiceUrl())
                .setAdminUrl(pulsarConfig.getAdminUrl())
                .setTopics(pulsarConfig.getSinkTopic())
                .setProducerName(pulsarConfig.getProducerName())
                .setSerializationSchema(new PulsarCommonSourceMessageSerializationSchema())
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE);

        sourceStream.sinkTo(pulsarSinkBuilder.build())
                .uid("sink-pulsar")
                .name("sink-pulsar");

        Properties props = new Properties();
        props.putAll(DEFAULT_PRODUCER_CONFIG);
        KafkaSinkBuilder<CommonSourceMessage> kafkaSinkBuilder = KafkaSink.<CommonSourceMessage>builder()
                .setKafkaProducerConfig(props)
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setTransactionalIdPrefix(kafkaConfig.getTransactionalIdPrefix())
                .setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT))
                .setProperty(TRANSACTION_TIMEOUT_CONFIG, kafkaConfig.getTransactionTimeoutMs())
                .setProperty(ENABLE_IDEMPOTENCE_CONFIG, "true")
                .setProperty(ACKS_CONFIG, "all")
                .setProperty("commit.offsets.on.checkpoint", "true")
                .setBootstrapServers(kafkaConfig.getBootstrapServers())
                .setRecordSerializer(new KafkaCommonSourceMessageSerializationSchema(kafkaConfig.getSinkTopic()));

        sourceStream.sinkTo(kafkaSinkBuilder.build())
                .uid("sink-kafka")
                .name("sink-kafka");

        env.execute(pulsarConfig.getJobName());
    }
}
