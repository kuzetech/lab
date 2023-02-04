package com.kuze.bigdata.study.streaming.listener;

import org.apache.spark.sql.streaming.StreamingQueryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyStreamingQueryListener extends StreamingQueryListener {

    private final static Logger logger = LoggerFactory.getLogger(MyStreamingQueryListener.class);

    @Override
    public void onQueryStarted(QueryStartedEvent event) {
        logger.info("----------我开始了");
    }

    // 这个方法不是线程安全的
    @Override
    public void onQueryProgress(QueryProgressEvent event) {
        logger.info("----------我处理中");
        System.out.println(event.progress().toString());
    }


    @Override
    public void onQueryTerminated(QueryTerminatedEvent event) {
        logger.info("----------我结束了");
    }
}
