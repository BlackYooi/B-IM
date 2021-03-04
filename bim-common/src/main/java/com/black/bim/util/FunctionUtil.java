package com.black.bim.util;

import java.util.function.Consumer;

/**
 * @description：
 * @author：8568
 */
public class FunctionUtil {

    public static  <T> void consumeIfValueNotNull(Consumer<T> consumer, T value) {
        if (null != value) {
            consumer.accept(value);
        }
    }

    public static  <T> void consumeIfValueNotNullOrThrow(Consumer<T> consumer, T value) {
        if (null == value) {
            throw new RuntimeException("所给参数不能为null");
        }
        consumer.accept(value);
    }
}
