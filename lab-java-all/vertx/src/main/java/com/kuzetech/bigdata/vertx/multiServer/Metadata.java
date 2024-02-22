package com.kuzetech.bigdata.vertx.multiServer;

import io.vertx.core.shareddata.Shareable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Metadata implements Shareable, Serializable {
    private String name;
}
