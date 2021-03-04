package com.black.bim.util;

import io.vavr.control.Try;

/**
 * @description：
 * @author：8568
 */
public class PackingTypeUtil {

    public static Integer parseIntOrNull(String value) {
        if (null == value) {
            return null;
        }
        return Try.of(() -> Integer.parseInt(value)).getOrNull();
    }

    public static Long parseLongOrNull(String value) {
        if (null == value) {
            return null;
        }
        return Try.of(() -> Long.parseLong(value)).getOrNull();
    }

    public static Boolean parseBooleanOrNull(String value) {
        if (null == value) {
            return null;
        }
        return Try.of(() -> Boolean.parseBoolean(value)).getOrNull();
    }

    public static Short parseShortOrNull(String value) {
        if (null == value) {
            return null;
        }
        return Try.of(() -> Short.parseShort(value)).getOrNull();
    }
}
