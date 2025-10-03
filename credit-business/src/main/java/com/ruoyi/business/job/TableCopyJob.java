package com.ruoyi.business.job;

import com.ruoyi.business.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TableCopyJob {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 源表名称
    private static final String SH_MQTT_DEVICE_RECENT_DATA = "sh_mqtt_device_recent_data";
    private static final String MQTT_TABLE_NAME = "sh_mqtt_device_data";
    // 源表名称
    private static final String SH_UDP_DEVICE_RECENT_DATA = "sh_udp_device_recent_data";
    private static final String UDP_TABLE_NAME = "sh_udp_device_data";

    /**
     * 每月最后一天23点59分执行表结构复制
     * 新表命名格式: sh_mqtt_device_recent_data_yyyyMM
     */
    @Scheduled(cron = "0 0 4 * * ? ")
    public void copyMqttTableStructure() {
        try {

            LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
            String monthStr = DateUtil.formatLocalDateTime(nextMonth,"yyyyMM");
            // 新表名称
            String targetTable = MQTT_TABLE_NAME + "_" + monthStr;

            // 执行表结构复制
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s LIKE %s", targetTable, SH_MQTT_DEVICE_RECENT_DATA);
            jdbcTemplate.execute(sql);

            System.out.println("表结构复制成功: " + targetTable + " 基于 " + SH_MQTT_DEVICE_RECENT_DATA);
        } catch (Exception e) {
            System.err.println("表结构复制失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 每月最后一天23点59分执行表结构复制
     * 新表命名格式: sh_mqtt_device_recent_data_yyyyMM
     */
    @Scheduled(cron = "0 0 4 * * ? ")
    public void copyUdpTableStructure() {
        try {

            LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
            String monthStr = DateUtil.formatLocalDateTime(nextMonth,"yyyyMM");
            // 新表名称
            String targetTable = UDP_TABLE_NAME + "_" + monthStr;

            // 执行表结构复制
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s LIKE %s", targetTable, SH_UDP_DEVICE_RECENT_DATA);
            jdbcTemplate.execute(sql);

            System.out.println("表结构复制成功: " + targetTable + " 基于 " + SH_UDP_DEVICE_RECENT_DATA);
        } catch (Exception e) {
            System.err.println("表结构复制失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
