package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.udsource.UserParallelSource;
import com.kuzetech.bigdata.flink.udsource.model.User;
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

public class UserJob {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment("UserJob", 2);

        DataStreamSource<User> source = env.addSource(new UserParallelSource(), "source");

        SingleOutputStreamOperator<UserCountryStatistics> stream = source.keyBy(User::getCountry)
                .process(new KeyedProcessFunction<>() {

                    private transient ValueState<UserCountryStatistics> statisticsValueState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        ValueStateDescriptor<UserCountryStatistics> descriptor =
                                new ValueStateDescriptor<>(
                                        "country-count",
                                        TypeInformation.of(new TypeHint<UserCountryStatistics>() {
                                        }));
                        statisticsValueState = getRuntimeContext().getState(descriptor);
                    }

                    @Override
                    public void processElement(
                            User value,
                            KeyedProcessFunction<String, User, UserCountryStatistics>.Context ctx,
                            Collector<UserCountryStatistics> out) throws Exception {
                        UserCountryStatistics statistics = statisticsValueState.value();
                        if (statistics == null) {
                            statistics = new UserCountryStatistics(value.getCountry(), 1);
                        } else {
                            statistics.setCount(statistics.getCount() + 1);
                        }
                        statisticsValueState.update(statistics);
                        out.collect(statistics);
                    }
                });

        stream.print();

        env.execute("UserJob");
    }
}
