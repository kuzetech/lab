package com.kuzetech.bigdata.flink.sink;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpBatchSink extends RichSinkFunction<String> implements CheckpointedFunction {

    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final Integer batchSize;
    private final String endpoint;

    private transient OkHttpClient client;
    private transient List<String> batch;

    public HttpBatchSink(Integer batchSize, String endpoint) {
        this.batchSize = batchSize;
        this.endpoint = endpoint;
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        batch = new ArrayList<>();
        client = new OkHttpClient.Builder()
                .addInterceptor(new HttpRetryInterceptor())
                .build();
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        if (!batch.isEmpty()) {
            sendBatch();
            batch.clear();
        }
    }

    @Override
    public void invoke(String value, Context context) throws Exception {
        batch.add(value);
        if (batch.size() >= batchSize) {
            sendBatch();
            batch.clear();
        }
    }

    @Override
    public void close() throws Exception {
        if (batch.size() >= batchSize) {
            sendBatch();
            batch.clear();
        }
    }

    private void sendBatch() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (String data : batch) {
            stringBuilder.append(data);
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");

        RequestBody body = RequestBody.create(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), JSON);

        Request request = new Request.Builder()
                .post(body)
                .url(endpoint)
                .build();

        Call call = client.newCall(request);
        try (Response res = call.execute()) {
            assert res.body() != null;
            if (res.isSuccessful()) {
                log.info("success send messages");
            } else {
                String errorMsg = String.format("executeBatchInsert error，code is %d, message is %s", res.code(), res.body());
                throw new RuntimeException(errorMsg);
            }
        }
    }
}
