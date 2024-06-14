package com.xmfunny.funnydb.flink.model;

import java.io.Serializable;
import java.util.function.Function;


/**
 * 事件值提取器
 *
 * @param <T>   事件
 * @param <OUT> 事件值
 */
public interface EventValueExtractor<T extends Event, OUT> extends Function<T, OUT>, Serializable {
}
