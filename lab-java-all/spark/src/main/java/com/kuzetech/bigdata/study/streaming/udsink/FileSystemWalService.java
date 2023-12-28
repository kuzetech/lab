package com.kuzetech.bigdata.study.streaming.udsink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.study.clickhouse.ClickHouseQueryConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileSystemWalService extends WalService {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemWalService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private transient FileSystem fs;
    private Path walPath;

    public FileSystemWalService(ClickHouseQueryConfig config, Configuration hadoopConfig) throws IOException {
        super(config);
        fs = FileSystem.get(hadoopConfig);
        walPath = new Path(super.config.getWalLocation());
        if (!fs.exists(walPath)) {
            FSDataOutputStream fsDataOutputStream = fs.create(walPath);
            fsDataOutputStream.close();
        }
    }

    public Wal getWal() throws IOException {
        FSDataInputStream open = fs.open(walPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(open));
        String content = br.readLine();
        open.close();
        if (content == null || content.trim().isEmpty()) {
            return null;
        } else {
            Wal wal = mapper.readValue(content, Wal.class);
            return wal;
        }
    }

    public void setWal(Wal wal) throws IOException {
        String content = mapper.writeValueAsString(wal);
        logger.info("写入 wal 的内容为 {}", content);
        FSDataOutputStream outputStream = fs.create(walPath, true);
        outputStream.writeBytes(content);
        outputStream.close();
    }

    public void deleteWal() throws IOException {
        FSDataOutputStream outputStream = fs.create(walPath, true);
        outputStream.writeBytes("");
        outputStream.close();
    }

    public void closeConnect() throws IOException {
        fs.close();
    }
}
