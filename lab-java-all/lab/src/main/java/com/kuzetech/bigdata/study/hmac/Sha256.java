package com.kuzetech.bigdata.study.hmac;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha256 {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        extracted("{\"batchId\":\"123\",\"messages\":[{\"type\":\"event\",\"data\":{\"a\":3,\"b\":\"3\"}}]}");
    }

    public static String extracted(String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec("secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        StringBuilder builder = new StringBuilder();
        builder.append("POST");
        builder.append("/v1/collect");
        builder.append("demo");
        builder.append("123");
        builder.append("123");
        builder.append(content);

        byte[] serverSign = hmacSha256.doFinal(builder.toString().getBytes());

        return Base64.getEncoder().encodeToString(serverSign);
    }

}
