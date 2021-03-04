package com.black.bim.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description：
 * @author：8568
 */
public class TimeUtil {

    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String getCurrentTimeStamp() {
        String format = TIME_FORMAT.format(new Date());
        return format;
    }
}
