package com.kuzetech.bigdata.flink;


import com.xmfunny.turbine.sdk.api.DefaultApi;
import com.xmfunny.turbine.sdk.invoker.ApiClient;
import com.xmfunny.turbine.sdk.invoker.ApiException;
import com.xmfunny.turbine.sdk.model.Resource;

import java.io.File;

public class UploadMetadataApp {
    public static void main(String[] args) throws ApiException {
        final ApiClient client = new ApiClient().setBasePath("http://localhost:8090");
        client.setUsername("turbine");
        client.setPassword("turbine123456");
        DefaultApi defaultApi = new DefaultApi(client);

        File file = new File("/Users/huangsw/code/lab/lab-java-all/lab-work/src/main/resources/metadata.json");
        if (!file.exists()) {
            throw new RuntimeException("file no exist");
        }

        Resource resource = defaultApi.upload("funnydb", "metadata-funnydb", file);
        System.out.println(resource.getKind());
    }
}
