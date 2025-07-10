package com.kuzetech.bigdata.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.flink.connector.pulsar.source.enumerator.PulsarSourceEnumState;
import org.apache.flink.connector.pulsar.source.enumerator.PulsarSourceEnumStateSerializer;
import org.apache.flink.core.memory.ByteArrayInputStreamWithPos;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.checkpoint.metadata.CheckpointMetadata;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.source.coordinator.SourceCoordinatorSerdeUtils;
import org.apache.flink.runtime.state.memory.ByteStreamStateHandle;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.apache.flink.runtime.checkpoint.Checkpoints.loadCheckpointMetadata;

@Slf4j
public class App {
    public static final int ILLEGAL_INPUT_ARGUMENT = -1;
    public static final int ILLEGAL_METADATA_FILEPATH = -2;

    public static void main(String[] args) throws IOException {
        // String savepointDirectory = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging";
        // String savepointDirectory = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob4Enrich";
        String savepointDirectory = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/17-work";

        File metaFile = new File(savepointDirectory + File.separator + "_metadata");
        if (!metaFile.exists() || !metaFile.isFile()) {
            log.error("Metafile {} is not a file or does not exist", metaFile.getPath());
            System.exit(ILLEGAL_METADATA_FILEPATH);
        }

        byte[] bytes = FileUtils.readFileToByteArray(metaFile);
        DataInputStream dataInputStream = new DataInputViewStreamWrapper(new ByteArrayInputStreamWithPos(bytes));
        CheckpointMetadata deserializedMetadata = loadCheckpointMetadata(dataInputStream, App.class.getClassLoader(), metaFile.getAbsolutePath());

        log.info("\n\n=========== Summary ===========");
        log.info("Checkpoint ID: {}", deserializedMetadata.getCheckpointId());
        log.info("Master States: {}", deserializedMetadata.getMasterStates());
        log.info("Operator States: {}", deserializedMetadata.getOperatorStates());

        log.info("\n\n=========== Operator States ===========");
        Collection<OperatorState> operatorStates = deserializedMetadata.getOperatorStates();
        log.info("Operator States Size: {}", operatorStates.size());

        log.info("\n\n=========== Coordinator States ===========");
        for (OperatorState originalOperatorState : deserializedMetadata.getOperatorStates()) {
            final ByteStreamStateHandle coordinatorState = originalOperatorState.getCoordinatorState();
            if (coordinatorState != null) {
                OperatorID operatorID = originalOperatorState.getOperatorID();
                final byte[] bs = coordinatorState.getData();
                log.info("Operator Id: {}, Coordinator State Size: {}", operatorID.toHexString(), bs.length);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bs);
                     DataInputStream in = new DataInputViewStreamWrapper(bais)) {
                    final int coordinatorSerdeVersion = readAndVerifyCoordinatorSerdeVersion(in);
                    int enumSerializerVersion = in.readInt();
                    int serializedEnumChkptSize = in.readInt();
                    byte[] serializedEnumChkpt = readBytes(in, serializedEnumChkptSize);

                    if (coordinatorSerdeVersion != SourceCoordinatorSerdeUtils.VERSION_0
                            && bais.available() > 0) {
                        throw new IOException("Unexpected trailing bytes in enumerator checkpoint data");
                    }

                    PulsarSourceEnumState enumState = PulsarSourceEnumStateSerializer.INSTANCE.deserialize(enumSerializerVersion, serializedEnumChkpt);
                    log.info("Enum State: {}", enumState);
                }
            }
        }
    }

    static int readAndVerifyCoordinatorSerdeVersion(DataInputStream in) throws IOException {
        int version = in.readInt();
        if (version > 1) {
            throw new IOException("Unsupported source coordinator serde version " + version);
        }
        return version;
    }

    static byte[] readBytes(DataInputStream in, int size) throws IOException {
        byte[] bytes = new byte[size];
        in.readFully(bytes);
        return bytes;
    }


}
