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

        String strContent = "{\"batchId\": \"123\",\"messages\":[{\"type\": \"Event\",\"data\": {\"#event\": \"login\",\"#identify\": \"user-fake428566\",\"#log_id\": \"f6399633-ce17-494e-a943-cec1873d5bdd\",\"#operate\": \"set\",\"#time\": 1699609490679,\"properties\": {\"#device_id\": \"device-fake1548091\",\"level\": 4509781237501530015,\"sex\": \"男\",\"user_last_login_time\": 1699609490679}}}]}";
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
