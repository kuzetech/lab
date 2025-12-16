package com.kuzetech.bigdata.flink.derive;

import com.kuzetech.bigdata.flink.derive.domain.AuOperatorKeyedState;
import com.kuzetech.bigdata.flink.derive.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.derive.function.AuOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.derive.function.AuOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.derive.function.IdentifyNewOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.derive.function.IdentifyNewOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.util.FlinkEnvironmentUtil;
import com.kuzetech.bigdata.flink.util.FlinkFsCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.core.fs.Path;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class DeriveWriteJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String deriveSavepointPath = parameterTool.getRequired("derive");
        String trackSavepointPath = parameterTool.getRequired("track");
        String targetSavepointPath = parameterTool.getRequired("target");

        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);

        StreamExecutionEnvironment env = FlinkEnvironmentUtil.getDefaultStreamExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                deriveSavepointPath,
                embeddedRocksDBStateBackend);

        DataStream<IdentifyNewOperatorKeyedState> newDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-process"),
                        new IdentifyNewOperatorKeyedStateReaderFunction(),
                        Types.STRING,
                        TypeInformation.of(IdentifyNewOperatorKeyedState.class))
                .filter(value -> value.getCreatedTs() != null);
        StateBootstrapTransformation<IdentifyNewOperatorKeyedState> newTransformation = OperatorTransformation
                .bootstrapWith(newDataStream)
                .keyBy(o -> o.key)
                .transform(new IdentifyNewOperatorKeyedStateBootstrapper());

        DataStream<AuOperatorKeyedState> dauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-dau-process"),
                        new AuOperatorKeyedStateReaderFunction("dau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null);
        StateBootstrapTransformation<AuOperatorKeyedState> dauTransformation = OperatorTransformation
                .bootstrapWith(dauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("dau-state"));

        DataStream<AuOperatorKeyedState> wauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-wau-process"),
                        new AuOperatorKeyedStateReaderFunction("wau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null);
        StateBootstrapTransformation<AuOperatorKeyedState> wauTransformation = OperatorTransformation
                .bootstrapWith(wauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("wau-state"));

        DataStream<AuOperatorKeyedState> mauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-mau-process"),
                        new AuOperatorKeyedStateReaderFunction("mau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null);
        StateBootstrapTransformation<AuOperatorKeyedState> mauTransformation = OperatorTransformation
                .bootstrapWith(mauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("mau-state"));

        SavepointWriter
                .fromExistingSavepoint(env, trackSavepointPath, embeddedRocksDBStateBackend)
                .withOperator(OperatorIdentifier.forUid("derive-event-process"), newTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-dau-process"), dauTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-wau-process"), wauTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-mau-process"), mauTransformation)
                .write(targetSavepointPath);

        env.execute("DeriveWriteJob");

        log.info("DeriveWriteJob run FlinkFsCopy");
        FlinkFsCopyUtil.copyDirIfNotExists(
                new Path(trackSavepointPath),
                new Path(targetSavepointPath)
        );
        log.info("DeriveWriteJob run FlinkFsCopy success");

    }
}
