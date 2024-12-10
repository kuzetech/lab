package com.kuzetech.bigdata.lab.uuid;

import java.nio.ByteBuffer;

public class App {
    public static void main(String[] args) {
        ByteBuffer timestamp = ByteBuffer.allocate(Long.BYTES);
        timestamp.putLong(System.currentTimeMillis());

        byte[] array = timestamp.array();
        for (byte b : array) {
            System.out.print(b + " "); // 输出以空格分隔
        }

        System.out.println();

        for (byte b : array) {
            System.out.printf("%02X ", b); // %02X表示两位大写十六进制，不足补0
        }
    }
}
