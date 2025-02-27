package com.kuzetech.bigdata.lab.hmac;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha256 {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        String strContent = "{\n" +
                "    \"batchId\": \"123\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"type\": \"Event\",\n" +
                "            \"data\": {\n" +
                "                \"#event\": \"#device_login\",\n" +
                "                \"#log_id\": \"638a4d25-09f7-4364-8cc6-8713277d30b4\",\n" +
                "                \"#time\": 1740637303355,\n" +
                "                \"#sdk_type\": \"Unity\",\n" +
                "                \"#sdk_version\": \"0.9.11\",\n" +
                "                \"#simulator\": false,\n" +
                "                \"#network\": \"4g\",\n" +
                "                \"#carrier\": \"\",\n" +
                "                \"#system_language\": \"zh_CN\",\n" +
                "                \"#zone_offset\": 8,\n" +
                "                \"#channel\": \"\",\n" +
                "                \"#os_platform\": \"android\",\n" +
                "                \"#device_id\": \"acb8b97b-c4e7-4a24-91c2-eefafdcc50cc\",\n" +
                "                \"#os_version\": \"12\",\n" +
                "                \"#device_model\": \"GM1900\",\n" +
                "                \"#manufacturer\": \"OnePlus\",\n" +
                "                \"#screen_height\": 832,\n" +
                "                \"#screen_width\": 1080\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        System.out.println(extracted("demo", "123", "123", strContent.getBytes(StandardCharsets.UTF_8)));
    }

    public static String extracted(
            String accessId,
            String nonce,
            String timestamp,
            byte[] content
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec("secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        byte[] methodBytes = "POST".getBytes(StandardCharsets.UTF_8);
        byte[] all = ArrayUtils.addAll(methodBytes, "/v1/collect".getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, accessId.getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, nonce.getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, timestamp.getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, content);

        byte[] serverSign = hmacSha256.doFinal(all);

        return Base64.getEncoder().encodeToString(serverSign);
    }

}
