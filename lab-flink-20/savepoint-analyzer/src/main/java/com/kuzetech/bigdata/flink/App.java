package com.kuzetech.bigdata.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.flink.core.memory.ByteArrayInputStreamWithPos;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.checkpoint.metadata.CheckpointMetadata;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import static org.apache.flink.runtime.checkpoint.Checkpoints.loadCheckpointMetadata;

@Slf4j
public class App {
    public static final int ILLEGAL_INPUT_ARGUMENT = -1;
    public static final int ILLEGAL_METADATA_FILEPATH = -2;

    public static void main(String[] args) throws IOException {
        String savepointDirectory = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/EventOperatorStateBuildJob";

        File metaFile = new File(savepointDirectory + File.separator + "_metadata");
        if (!metaFile.exists() || !metaFile.isFile()) {
            log.error("Metafile {} is not a file or does not exist", metaFile.getPath());
            System.exit(ILLEGAL_METADATA_FILEPATH);
        }

        byte[] bytes = FileUtils.readFileToByteArray(metaFile);
        DataInputStream in = new DataInputViewStreamWrapper(new ByteArrayInputStreamWithPos(bytes));
        CheckpointMetadata deserializedMetadata = loadCheckpointMetadata(in, App.class.getClassLoader(), metaFile.getAbsolutePath());

        log.info("\n\n=========== Summary ===========");
        log.info("Checkpoint ID: {}", deserializedMetadata.getCheckpointId());
        log.info("Master States: {}", deserializedMetadata.getMasterStates());
        log.info("Operator States: {}", deserializedMetadata.getOperatorStates());

    }


}
