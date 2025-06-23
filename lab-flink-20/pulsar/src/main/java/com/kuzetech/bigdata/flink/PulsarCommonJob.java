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

import com.kuzetech.bigdata.flink.pulsar.PulsarConfig;
import com.kuzetech.bigdata.flink.pulsar.PulsarMessage;
import com.kuzetech.bigdata.flink.pulsar.PulsarUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.pulsar.source.PulsarSourceBuilder;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class PulsarCommonJob {

	public static void main(String[] args) throws Exception {

		ParameterTool parameterTool = ParameterTool.fromArgs(args);

		final StreamExecutionEnvironment env = FlinkUtil.initEnv(parameterTool);

		final PulsarConfig pulsarConfig = PulsarConfig.generateFromParameterTool(parameterTool);

		PulsarSourceBuilder<PulsarMessage> pulsarSourceBaseBuilder = PulsarUtil.buildPulsarSourceBaseBuilder(pulsarConfig);

		SingleOutputStreamOperator<PulsarMessage> input =
				env.fromSource(pulsarSourceBaseBuilder.build(), WatermarkStrategy.noWatermarks(), "source")
						.uid("source")
						.name("source");

		env.execute("PulsarCommonJob");
	}
}
