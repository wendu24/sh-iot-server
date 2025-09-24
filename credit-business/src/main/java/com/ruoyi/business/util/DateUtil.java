package com.ruoyi.business.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {


    // 常用日期时间格式
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";

    /**
     * 将 Date 格式化为指定格式的字符串
     *
     * @param date    要格式化的 Date 对象
     * @param pattern 格式化模式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * 将时间戳（毫秒）转换为 LocalDateTime。
     *
     * @param timestampMillis 时间戳，单位为毫秒。
     * @return 转换后的 LocalDateTime 对象。
     */
    public static LocalDateTime timestampToLocalDateTime(long timestampMillis) {
        // Instant.ofEpochMilli() 创建一个 Instant 对象，表示从 1970-01-01T00:00:00Z 开始的毫秒数。
        // atZone() 将 Instant 转换为带时区的 ZonedDateTime。
        // toLocalDateTime() 从 ZonedDateTime 中提取不带时区的 LocalDateTime 部分。
        return Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将字符串解析为 Date 对象
     *
     * @param dateString 要解析的字符串
     * @param pattern    字符串的日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 解析后的 Date 对象
     */
    public static Date parseDate(String dateString, String pattern) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    // --- Java 8 LocalDateTime / LocalDate 与 Date 的互转 ---

    /**
     * 将 Date 转换为 LocalDateTime
     *
     * @param date 要转换的 Date 对象
     * @return 转换后的 LocalDateTime 对象
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 Date 转换为 LocalDate
     *
     * @param date 要转换的 Date 对象
     * @return 转换后的 LocalDate 对象
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 LocalDateTime 转换为 Date
     *
     * @param localDateTime 要转换的 LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 LocalDate 转换为 Date
     *
     * @param localDate 要转换的 LocalDate 对象
     * @return 转换后的 Date 对象
     */
    public static Date localDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
