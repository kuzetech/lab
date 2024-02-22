package com.kuzetech.bigdata.vertx.multiServer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class MetadataVerticle extends AbstractVerticle {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();

        vertx.setPeriodic(10000, id -> {
            Metadata metadata = new Metadata();
            metadata.setName(sdf.format(new Date()));
            eventBus.publish("news.uk.sport", metadata);
        });
    }
}
