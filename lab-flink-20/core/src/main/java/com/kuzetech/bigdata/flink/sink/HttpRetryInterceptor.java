package com.kuzetech.bigdata.flink.sink;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class HttpRetryInterceptor implements Interceptor {

    private static final int maxRetries = 3;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        int retryCount = 0;
        Response response = null;
        while (retryCount < maxRetries) {
            try {
                response = chain.proceed(chain.request());
                retryCount++;
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw e;
                }
            }
        }
        return response;
    }
}