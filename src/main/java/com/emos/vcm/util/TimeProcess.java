package com.emos.vcm.util;

import java.util.Date;

public class TimeProcess {
    public static Date add(Date date, double hours) {
        return new Date(date.getTime() + (long) (hours * 60 * 60 * 1000));
    }

    // 计算两个时间之间相差的小时
    public static double difference(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime()) / (1000.0 * 60.0 * 60.0);
    }
}
