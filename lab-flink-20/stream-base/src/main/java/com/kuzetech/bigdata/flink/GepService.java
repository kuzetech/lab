package com.kuzetech.bigdata.flink;

import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;

import java.io.*;

public class GepService {

    private DatabaseReader createDatabaseReader(String res) {
        File tempFile;
        try {
            tempFile = File.createTempFile("geoip-", ".mmdb");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (
                InputStream in = this.getClass().getResourceAsStream(res);
                OutputStream out = new FileOutputStream(tempFile)) {
            if (in == null) {
                throw new RuntimeException(res + " does no exist");
            }
            in.transferTo(out);

            // 使用 MEMORY_MAPPED 的降低内存使用，需要输入的文件存在文件系统中
            return new DatabaseReader.Builder(tempFile).fileMode(Reader.FileMode.MEMORY_MAPPED).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
