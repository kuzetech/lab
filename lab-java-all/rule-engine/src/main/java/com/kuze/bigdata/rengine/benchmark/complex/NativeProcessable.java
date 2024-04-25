package com.kuze.bigdata.rengine.benchmark.complex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class NativeProcessable implements com.kuze.bigdata.rengine.benchmark.complex.Processable {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    @Override
    public Boolean filter(int i) {
        return (i % 2 == 0);
    }

    @Override
    public void process(int i) throws JsonProcessingException {
        com.kuze.bigdata.rengine.benchmark.complex.Event e = new com.kuze.bigdata.rengine.benchmark.complex.Event();
        e.setId(UUID.randomUUID().toString());
        e.setName("Login");
        e.setTime(System.currentTimeMillis());
        e.setIp(com.kuze.bigdata.rengine.benchmark.complex.Event.generateRandomIPAddress());

        ObjectNode objectNode = (ObjectNode) mapper.valueToTree(e);
        long milSecond = objectNode.get("time").asLong();
        objectNode.put("second_time", milSecond / 1000);

        byte[] hash = md.digest(e.getIp().getBytes(StandardCharsets.UTF_8));
        // 将 byte 数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        objectNode.put("id_hash", hexString.toString());

        mapper.writeValueAsString(objectNode);
    }
}
