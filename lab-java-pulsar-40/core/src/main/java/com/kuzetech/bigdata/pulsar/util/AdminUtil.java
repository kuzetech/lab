package com.kuzetech.bigdata.pulsar.util;

import com.kuzetech.bigdata.pulsar.constant.BaseConstant;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SizeUnit;

import java.util.concurrent.TimeUnit;

public class AdminUtil {
    public static PulsarAdmin createDefaultLocalAdmin() throws PulsarClientException {
        return PulsarAdmin.builder()
                .serviceHttpUrl(BaseConstant.DEFAULT_LOCAL_ADMIN_URL)
                .build();
    }
}
