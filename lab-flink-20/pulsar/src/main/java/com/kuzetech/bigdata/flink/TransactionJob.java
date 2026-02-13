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
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.connector.pulsar.sink.PulsarSinkBuilder;
import org.apache.flink.connector.pulsar.sink.PulsarSinkOptions;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.PulsarSourceBuilder;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.pulsar.client.api.CompressionType;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TransactionJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = FlinkUtil.createCheckpointEnv();

        PulsarSourceBuilder<String> sourceBuilder = PulsarSource.builder()
                .setServiceUrl("pulsar://localhost:6650")
                .setTopics("persistent://public/default/source")
                .setStartCursor(StartCursor.earliest())
                .setDeserializationSchema(new SimpleStringSchema())
                .setSubscriptionName("test-subscriber");

        SingleOutputStreamOperator<String> sourceStream = env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "source")
                .uid("source")
                .name("source");

        SingleOutputStreamOperator<String> processStream = sourceStream.map(value -> {
                    System.out.println("Processing: " + value);
                    return value;
                })
                .uid("process")
                .name("process");

        PulsarSinkBuilder<String> sinkBuilder = PulsarSink.builder()
                .setServiceUrl("pulsar://localhost:6650")
                .setAdminUrl("http://localhost:8080")
                .setTopics("persistent://public/default/sink")
                .setProducerName("transaction-producer")
                .setSerializationSchema(new SimpleStringSchema())
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setConfig(PulsarSinkOptions.PULSAR_COMPRESSION_TYPE, CompressionType.LZ4)
                .setConfig(PulsarSinkOptions.PULSAR_BATCHING_MAX_PUBLISH_DELAY_MICROS, MILLISECONDS.toMicros(50))
                .setConfig(PulsarSinkOptions.PULSAR_BATCHING_MAX_BYTES, 10 * 1024 * 1024);

        processStream.sinkTo(sinkBuilder.build())
                .uid("sink")
                .name("sink");

        env.execute("transaction job");
    }
}
