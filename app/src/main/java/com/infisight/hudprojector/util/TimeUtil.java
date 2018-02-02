package com.infisight.hudprojector.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by fawn on 2015/10/10.
 */
public class TimeUtil {
    private static DateTimeFormatter mmssFormatter = DateTimeFormat.forPattern("mm:ss");
    private static DateTimeFormatter HHmmssFormatter = DateTimeFormat.forPattern("HH:mm:ss");

    public static String periodToHHMMss(int duration){
        return HHmmssFormatter.print(duration);
    }
    public static String periodToMMss(int duration){
        return mmssFormatter.print(duration);
    }
}
