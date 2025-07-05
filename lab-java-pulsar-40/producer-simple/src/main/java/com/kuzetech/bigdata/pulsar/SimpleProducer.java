package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SimpleProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = ProducerUtil.getSimpleProducer(client, "funnydb-ingest-receive");
        ) {
            String data = "{\n" +
                    "  \"type\": \"Event\",\n" +
                    "  \"ip\": \"0:0:0:0:0:0:0:1\",\n" +
                    "  \"app\": \"demo\",\n" +
                    "  \"data\": {\n" +
                    "    \"#event\": \"#device_login\",\n" +
                    "    \"#log_id\": \"638a4d25-09f7-4364-8cc6-8713277d30b4\",\n" +
                    "    \"#time\": %d,\n" +
                    "    \"#sdk_type\": \"Unity\",\n" +
                    "    \"#sdk_version\": \"0.9.11\",\n" +
                    "    \"#simulator\": false,\n" +
                    "    \"#network\": \"4g\",\n" +
                    "    \"#carrier\": \"\",\n" +
                    "    \"#system_language\": \"zh_CN\",\n" +
                    "    \"#zone_offset\": 8,\n" +
                    "    \"#channel\": \"\",\n" +
                    "    \"#os_platform\": \"android\",\n" +
                    "    \"#device_id\": \"acb8b97b-c4e7-4a24-91c2-eefafdcc50cc\",\n" +
                    "    \"#os_version\": \"12\",\n" +
                    "    \"#device_model\": \"GM1900\",\n" +
                    "    \"#manufacturer\": \"OnePlus\",\n" +
                    "    \"#screen_height\": 832,\n" +
                    "    \"#screen_width\": 1080\n" +
                    "  },\n" +
                    "  \"access_id\": \"demo\",\n" +
                    "  \"ingest_time\": 1741257985601\n" +
                    "}";


            byte[] content = data.getBytes(StandardCharsets.UTF_8);

            Map<String, String> properties = new HashMap<>();
            properties.put("log_id", "638a4d25-09f7-4364-8cc6-8713277d30b4");
            properties.put("app", "demo");
            properties.put("event", "#device_login");

            for (int i = 0; i < 10000; i++) {
                producer.newMessage()
                        .key(String.valueOf(i))
                        .value(content)
                        .properties(properties)
                        .sendAsync();
            }
        }
    }
}
