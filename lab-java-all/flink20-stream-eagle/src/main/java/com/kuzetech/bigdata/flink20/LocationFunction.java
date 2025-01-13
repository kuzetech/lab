package com.kuzetech.bigdata.flink20;

import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class LocationFunction extends RichAsyncFunction<DataBean, DataBean> {

    private transient CloseableHttpAsyncClient httpAsyncClient;

    @Override
    public void open(OpenContext openContext) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom().build();
        httpAsyncClient = HttpAsyncClients.custom()
                .setMaxConnTotal(10)
                .setDefaultRequestConfig(requestConfig)
                .build();
        httpAsyncClient.start();
    }

    @Override
    public void close() throws Exception {
        httpAsyncClient.close();
    }

    @Override
    public void asyncInvoke(DataBean input, ResultFuture<DataBean> resultFuture) throws Exception {
        HttpGet httpGet = new HttpGet("");
        Future<HttpResponse> future = httpAsyncClient.execute(httpGet, null);
        CompletableFuture.supplyAsync(new Supplier<DataBean>() {
            @Override
            public DataBean get() {
                try {
                    HttpResponse response = future.get();
                    if (response.getStatusLine().getStatusCode() == 200) {
                        // json 字符串
                        String result = EntityUtils.toString(response.getEntity());
                        // ...input.set
                    }
                    return input;
                } catch (Exception e) {
                    return null;
                }
            }
        }).thenAccept((DataBean result) -> {
            resultFuture.complete(Collections.singleton(result));
        });
    }
}
