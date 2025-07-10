package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class IngestProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<byte[]> producer = ProducerUtil.getIngestProducer(client, "funnydb-ingest-receive");
        ) {
            String template = """
                    {
                        "type": "Event",
                        "ip": "0:0:0:0:0:0:0:1",
                        "app": "demo",
                        "access_id": "demo",
                        "ingest_time": 1741257985601,
                        "data": {
                            "#event": "#device_login",
                            "#log_id": "638a4d25-09f7-4364-8cc6-8713277d30b42",
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


            String result = String.format(template, System.currentTimeMillis());
            byte[] content = result.getBytes(StandardCharsets.UTF_8);

            Map<String, String> headers = new HashMap<>();
            headers.put("log_id", "638a4d25-09f7-4364-8cc6-8713277d30b42");
            headers.put("event", "#device_login");
            headers.put("app", "demo");

            producer.newMessage()
                    .properties(headers)
                    .value(content)
                    .sendAsync();
        }
    }


}
