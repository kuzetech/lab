package org.example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyConfig {

    private final ReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    private String outputTopic;

    public MyConfig(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public String getOutputTopic() {
        r.lock();
        try {
            return outputTopic;
        } finally {
            r.unlock();
        }
    }

    public void setOutputTopic(String outputTopic) {
        w.lock();
        try {
            this.outputTopic = outputTopic;
        } finally {
            w.unlock();
        }
    }
}
