package com.kuzetech.bigdata.flink.udsource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAndTimeEvent {

    private final static Random r = new Random();

    @Getter
    public enum KeyCategory {
        CATEGORY1("1"), CATEGORY2("2"), CATEGORY3("3");

        private KeyCategory(String name) {
            this.name = name;
        }

        private final String name;
    }

    private String key;
    /**
     * 事件时间，精确到毫秒
     */
    private Long time;

    public static KeyAndTimeEvent generate() {
        return new KeyAndTimeEvent(
                KeyCategory.values()[r.nextInt(KeyCategory.values().length)].getName(),
                System.currentTimeMillis());
    }
}
