package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.udsource.DeviceParallelSource;
import com.kuzetech.bigdata.flink.udsource.model.Device;
import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class DeviceJob {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment("DeviceJob", 2);

        DataStreamSource<Device> source = env.addSource(new DeviceParallelSource(), "source");

        SingleOutputStreamOperator<DeviceModelStatistics> stream = source.keyBy(Device::getModel)
                .process(new KeyedProcessFunction<>() {

                    private transient ValueState<DeviceModelStatistics> statisticsValueState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        ValueStateDescriptor<DeviceModelStatistics> descriptor =
                                new ValueStateDescriptor<>(
                                        "model-count",
                                        TypeInformation.of(new TypeHint<DeviceModelStatistics>() {
                                        }));
                        statisticsValueState = getRuntimeContext().getState(descriptor);
                    }

                    @Override
                    public void processElement(
                            Device value,
                            KeyedProcessFunction<String, Device, DeviceModelStatistics>.Context ctx,
                            Collector<DeviceModelStatistics> out) throws Exception {
                        DeviceModelStatistics statistics = statisticsValueState.value();
                        if (statistics == null) {
                            statistics = new DeviceModelStatistics(value.getModel(), 1);
                        } else {
                            statistics.setCount(statistics.getCount() + 1);
                        }
                        statisticsValueState.update(statistics);
                        out.collect(statistics);
                    }
                });

        stream.print();

        env.execute("DeviceJob");
    }
}
