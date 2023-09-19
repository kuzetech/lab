package com.kuzetech.bigdata.study.hmac;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha256 {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        String strContent = "{\"batchId\":\"123\",\"messages\":[{\"type\":\"event\",\"data\":{\"a\":3,\"b\":\"3\"}}]}";
        extracted(strContent.getBytes(StandardCharsets.UTF_8));
    }

    public static String extracted(byte[] content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec("secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        byte[] methodBytes = "POST".getBytes(StandardCharsets.UTF_8);
        byte[] all = ArrayUtils.addAll(methodBytes, "/v1/collect".getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, "demo".getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, "123".getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, "123".getBytes(StandardCharsets.UTF_8));
        all = ArrayUtils.addAll(all, content);

        byte[] serverSign = hmacSha256.doFinal(all);

        return Base64.getEncoder().encodeToString(serverSign);
    }

}
