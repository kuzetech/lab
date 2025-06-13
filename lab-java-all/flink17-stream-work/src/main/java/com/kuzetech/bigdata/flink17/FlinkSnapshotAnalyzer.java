package com.kuzetech.bigdata.flink17;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.memory.ByteArrayInputStreamWithPos;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.StateObjectCollection;
import org.apache.flink.runtime.checkpoint.metadata.CheckpointMetadata;
import org.apache.flink.runtime.state.*;
import org.apache.flink.runtime.state.memory.ByteStreamStateHandle;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

import static org.apache.flink.runtime.checkpoint.Checkpoints.loadCheckpointMetadata;

@Slf4j
public class FlinkSnapshotAnalyzer {
    public static final int ILLEGAL_INPUT_ARGUMENT = -1;
    public static final int ILLEGAL_METADATA_FILEPATH = -2;

    public static void main(String[] args) throws IOException {
        /*if (args.length == 0) {
            log.error("Please input a Flink snapshot path as the first argument");
            System.exit(ILLEGAL_INPUT_ARGUMENT);
        }*/

        // String savepointDirectory = "/Users/huangsw/code/lab/lab-java-all/flink17-steam/data/sps/savepoint-d0212c-1b0fba90966c";
        String savepointDirectory = "/Users/huangsw/code/lab/lab-java-all/flink17-steam/data/cks/f1cb4d3807ee855cbcdb83f0c2308525/chk-2";
        log.info("User has provided snapshot path {}", savepointDirectory);

        File metaFile = new File(savepointDirectory + File.separator + "_metadata");
        if (!metaFile.exists() || !metaFile.isFile()) {
            log.error("Metafile {} is not a file or does not exist", metaFile.getPath());
            System.exit(ILLEGAL_METADATA_FILEPATH);
        }

        byte[] bytes = FileUtils.readFileToByteArray(metaFile);
        DataInputStream in = new DataInputViewStreamWrapper(new ByteArrayInputStreamWithPos(bytes));
        CheckpointMetadata deserializedMetadata = loadCheckpointMetadata(in, FlinkSnapshotAnalyzer.class.getClassLoader(), metaFile.getAbsolutePath());

        log.info("\n\n=========== Summary ===========");
        log.info("Checkpoint ID: {}", deserializedMetadata.getCheckpointId());
        log.info("Master States: {}", deserializedMetadata.getMasterStates());
        log.info("Operator States: {}", deserializedMetadata.getOperatorStates());

        PriorityQueue<Tuple2<OperatorState, Long>> stateSizeQueue = new PriorityQueue<>(new OperatorStateSizeComparator());

        double totalStateSizes = 0;
        for (OperatorState operatorState : deserializedMetadata.getOperatorStates()) {
            long stateSizeForOperator = 0;
            for (Map.Entry<Integer, OperatorSubtaskState> entry : operatorState.getSubtaskStates().entrySet()) {
                totalStateSizes += entry.getValue().getStateSize();
                stateSizeForOperator += entry.getValue().getStateSize();
            }
            stateSizeQueue.add(new Tuple2<>(operatorState, stateSizeForOperator));
        }

        while (!stateSizeQueue.isEmpty()) {
            Tuple2<OperatorState, Long> operatorStateTuple = stateSizeQueue.poll();
            OperatorState operatorState = operatorStateTuple.f0;
            log.info("\n\n-> [{}%] {}MB Operator ID {}",
                    operatorStateTuple.f1 / totalStateSizes * 100,
                    operatorStateTuple.f1 / 1024 / 1024,
                    operatorState.getOperatorID());

            for (Map.Entry<Integer, OperatorSubtaskState> entry : operatorState.getSubtaskStates().entrySet()) {
                OperatorSubtaskState subtaskState = entry.getValue();
                // log.info("SubtaskState : {}", subtaskState);
                log.info("Subtask {} : {} B",
                        entry.getKey(),
                        entry.getValue().getStateSize());

                StateObjectCollection<KeyedStateHandle> managedKeyedState = subtaskState.getManagedKeyedState();
                for (KeyedStateHandle keyedStateHandle : managedKeyedState) {
                    if (keyedStateHandle instanceof IncrementalRemoteKeyedStateHandle) {
                        IncrementalRemoteKeyedStateHandle handle = (IncrementalRemoteKeyedStateHandle) keyedStateHandle;
                        log.info("    keyGroupRange : {}", handle.getKeyGroupRange());

                        log.info("        SharedState :");
                        Map<StateHandleID, StreamStateHandle> sharedStateMap = handle.getSharedState();
                        for (Map.Entry<StateHandleID, StreamStateHandle> stateHandleIDStreamStateHandleEntry : sharedStateMap.entrySet()) {
                            log.info("            handleID   : {}", stateHandleIDStreamStateHandleEntry.getKey());
                            ByteStreamStateHandle value = (ByteStreamStateHandle) stateHandleIDStreamStateHandleEntry.getValue();
                            log.info("            handleName : {}", value.getHandleName().split("cks")[1]);
                            log.info("            handleSize : {}", value.getStateSize());
                        }

                        log.info("        PrivateState :");
                        Map<StateHandleID, StreamStateHandle> privateStateMap = handle.getPrivateState();
                        for (Map.Entry<StateHandleID, StreamStateHandle> stateHandleIDStreamStateHandleEntry : privateStateMap.entrySet()) {
                            log.info("            handleID   : {}", stateHandleIDStreamStateHandleEntry.getKey());
                            ByteStreamStateHandle value = (ByteStreamStateHandle) stateHandleIDStreamStateHandleEntry.getValue();
                            log.info("            handleName : {}", value.getHandleName().split("cks")[1]);
                            log.info("            handleSize : {}", value.getStateSize());
                        }

                        log.info("        MetaState    :");
                        ByteStreamStateHandle metaStateHandle = (ByteStreamStateHandle) handle.getMetaStateHandle();
                        log.info("            handleID   : the same handle name");
                        log.info("            handleName : {}", metaStateHandle.getHandleName().split("cks")[1]);
                        log.info("            handleSize : {}", metaStateHandle.getStateSize());
                    } else if (keyedStateHandle instanceof KeyGroupsSavepointStateHandle) {
                        KeyGroupsSavepointStateHandle handle = (KeyGroupsSavepointStateHandle) keyedStateHandle;

                        ByteStreamStateHandle value = (ByteStreamStateHandle) handle.getDelegateStateHandle();
                        log.info("        handleName  : {}", value.getHandleName().split("sps")[1]);
                        log.info("        handleSize  : {}", value.getStateSize());

                        KeyGroupRangeOffsets keyGroupRangeOffsets = handle.getGroupRangeOffsets();
                        KeyGroupRange keyGroupRange = keyGroupRangeOffsets.getKeyGroupRange();
                        log.info("        keyGroup    : {}", keyGroupRange);
                        for (Integer keyGroup : keyGroupRange) {
                            log.info("            group {} : begin offset {}", keyGroup, keyGroupRangeOffsets.getKeyGroupOffset(keyGroup));
                        }
                    }
                }

            }
        }
    }

    public static class OperatorStateSizeComparator implements Comparator<Tuple2<OperatorState, Long>> {
        @Override
        public int compare(Tuple2<OperatorState, Long> o1, Tuple2<OperatorState, Long> o2) {
            return Long.compare(o1.f1, o2.f1);
        }
    }
}
