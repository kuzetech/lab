package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IngestProducer {


    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<byte[]> producer = ProducerUtil.getIngestProducer(client, "funnydb-ingest-receive");
        ) {
            for (int i = 0; i < 1000; i++) {
                Long ingestTimeMs = System.currentTimeMillis();
                Long eventTimeMs = ingestTimeMs - 1000;
                String logId = UUID.randomUUID().toString();

                byte[] messageContent = String.format(template, ingestTimeMs, logId, eventTimeMs).getBytes(StandardCharsets.UTF_8);
                headers.put("log_id", logId);

                producer.newMessage()
                        .properties(headers)
                        .value(messageContent)
                        .send();

                Thread.sleep(200);
            }
        }
    }

    private static final Map<String, String> headers = new HashMap<>();
    static {
        headers.put("app", "demo");
        headers.put("event", "#device_login");
    }

    private static final String template = """
                    {
                        "type": "Event",
                        "ip": "0:0:0:0:0:0:0:1",
                        "app": "demo",
                        "access_id": "demo",
                        "ingest_time": %d,
                        "data": {
                            "#event": "#device_login",
                            "#log_id": "%s",
                            "#sdk_type": "Unity",
                            "#sdk_version": "0.9.11",
                            "#simulator": false,
                            "#network": "4g",
                            "#carrier": "",
                            "#system_language": "zh_CN",
                            "#zone_offset": 8,
                            "#channel": "",
                            "#os_platform": "android",
                            "#device_id": "acb8b97b-c4e7-4a24-91c2-eefafdcc50cc",
                            "#os_version": "12",
                            "#device_model": "GM1900",
                            "#manufacturer": "OnePlus",
                            "#screen_height": 832,
                            "#screen_width": 1080,
                            "#time": %d
                        }
                    }
                    """;
}
