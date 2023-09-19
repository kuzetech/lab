package com.kuzetech.bigdata.study.gzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipApp {

    public static void main(String[] args) throws IOException {

        String content = "{\"batchId\":\"123\",\"messages\":[{\"type\":\"event\",\"data\":{\"a\":3,\"b\":\"3\"}}]}";

        byte[] result = compress(content);

        String source = uncompress(result);

        System.out.println(source);

    }

    public static byte[] compress(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);

        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();

        return out.toByteArray();
    }

    public static String uncompress(byte[] source) throws IOException {
        if (source == null || source.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(source);

        GZIPInputStream ungzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }

        return out.toString(StandardCharsets.UTF_8.name());
    }


}
