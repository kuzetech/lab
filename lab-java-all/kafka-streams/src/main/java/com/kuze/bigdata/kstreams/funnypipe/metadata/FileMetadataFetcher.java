package com.kuze.bigdata.kstreams.funnypipe.metadata;

import com.kuze.bigdata.kstreams.funnypipe.utils.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileMetadataFetcher {

    private static final Logger log = LoggerFactory.getLogger(FileMetadataFetcher.class);
    private static final String CONFIG_FILE_NAME = "metadata.json";

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private boolean isRunning = true;

    private final WatchService watcher;
    private MetaData[] currentConfig;

    public FileMetadataFetcher(Properties envProps) throws IOException {
        String watchDirPath = envProps.getProperty("metadata.watch.dir");
        Integer freshMS = Integer.parseInt(envProps.getProperty("metadata.watch.fresh.ms"));
        this.watcher = FileSystems.getDefault().newWatchService();
        Paths.get(watchDirPath).register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

        String content = IOUtils.toString(new FileReader(watchDirPath + "/" + CONFIG_FILE_NAME));
        currentConfig = JsonUtil.mapper.readValue(content, MetaData[].class);
        log.info("init metadata success");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    WatchKey key = null;
                    try {
                        key = watcher.poll(freshMS, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        log.error("InterruptedException", e);
                        throw new RuntimeException(e);
                    }

                    if (key == null) {
                        continue;
                    }

                    // key.pollEvents()用于获取文件变化事件，只能获取一次，不能重复获取，类似队列的形式。
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path fileName = (Path) event.context();
                        if (fileName.toFile().getName().equalsIgnoreCase(CONFIG_FILE_NAME)) {
                            MetaData[] metadataArray;
                            try {
                                String content = IOUtils.toString(new FileReader(watchDirPath + "/" + fileName));
                                metadataArray = JsonUtil.mapper.readValue(content, MetaData[].class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            rwl.writeLock().lock(); // 获取写锁
                            try {
                                currentConfig = metadataArray;
                            } finally {
                                rwl.writeLock().unlock(); // 释放写锁
                            }
                            log.info("discover metadata change and reload success");
                        }
                    }
                    // 每次调用WatchService的take()或poll()方法时需要通过本方法重置
                    if (!key.reset()) {
                        break;
                    }
                }
            }
        }).start();
    }

    public MetaData[] getCurrentConfig() {
        rwl.readLock().lock(); // 获取读锁
        try {
            return currentConfig;
        } finally {
            rwl.readLock().unlock(); // 释放读锁
        }
    }

    public void close() {
        this.isRunning = false;
    }

}
