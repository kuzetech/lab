package com.kuzetech.bigdata.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.pulsar.source.enumerator.PulsarSourceEnumState;
import org.apache.flink.connector.pulsar.source.enumerator.PulsarSourceEnumStateSerializer;
import org.apache.flink.connector.pulsar.source.enumerator.topic.TopicPartition;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.checkpoint.MasterState;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.checkpoint.metadata.CheckpointMetadata;
import org.apache.flink.runtime.state.memory.ByteStreamStateHandle;
import org.apache.flink.state.api.runtime.SavepointLoader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class SavepointAnalyzer {
    public static void main(String[] args) throws IOException {
        String metadataPath = "/Users/huangsw/Downloads/_metadata";
//        String metadataPath = "/Users/huangsw/code/lab/lab-flink-20/state-processor-demo/data/track/20/fix/_metadata";
//        String metadataPath = "/Users/huangsw/code/lab/lab-flink-20/state-processor-demo/data/track/staging/_metadata";

        CheckpointMetadata checkpointMetadata = SavepointLoader.loadSavepointMetadata(metadataPath);
        Collection<MasterState> masterStates = checkpointMetadata.getMasterStates();
        Collection<OperatorState> operatorStates = checkpointMetadata.getOperatorStates();

        log.info("\n\n=========== Summary ===========");
        log.info("Checkpoint ID: {}", checkpointMetadata.getCheckpointId());
        log.info("Master States Number: {}", masterStates.size());
        log.info("Operator States Number: {}", operatorStates.size());

        log.info("\n\n=========== Detail States ===========");
        for (OperatorState originalOperatorState : operatorStates) {
            final ByteStreamStateHandle coordinatorState = originalOperatorState.getCoordinatorState();
            log.info("operatorID: {}, maxParallelism: {}, totalSize: {}, coordinatorStateSize: {}",
                    originalOperatorState.getOperatorID(),
                    originalOperatorState.getMaxParallelism(),
                    originalOperatorState.getStateSize(),
                    coordinatorState == null ? 0 : coordinatorState.getStateSize()
            );
            if (coordinatorState != null && coordinatorState.getStateSize() > 0) {
                final byte[] bs = coordinatorState.getData();
                try (
                        ByteArrayInputStream bais = new ByteArrayInputStream(bs);
                        DataInputStream in = new DataInputViewStreamWrapper(bais)
                ) {
                    int coordinatorSerdeVersion = in.readInt();
                    int enumSerializerVersion = in.readInt();
                    int serializedEnumChkptSize = in.readInt();
                    byte[] serializedEnumChkpt = readBytes(in, serializedEnumChkptSize);

                    PulsarSourceEnumState enumState = PulsarSourceEnumStateSerializer.INSTANCE.deserialize(enumSerializerVersion, serializedEnumChkpt);
                    Set<TopicPartition> appendedPartitions = enumState.getAppendedPartitions();
                    log.info("      Pulsar Enum State AppendedPartitions:");
                    appendedPartitions.forEach(topicPartition -> {
                        log.info("              {}", topicPartition);
                    });
                } catch (Exception e) {
                    //log.error("Failed to deserialize Pulsar Enum State", e);
                }
            }
        }
    }

    static byte[] readBytes(DataInputStream in, int size) throws IOException {
        byte[] bytes = new byte[size];
        in.readFully(bytes);
        return bytes;
    }


}
