package com.ruoyi.business.job;

import com.ruoyi.business.util.DateUtil;
import com.ruoyi.common.constant.TableNameConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TableCopyJob {

    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * 每天都会执行,避免漏了,表存在额不会重复创建
     * 新表命名格式: sh_mqtt_device_recent_data_yyyyMM
     */
    @Scheduled(cron = "0 0 4 * * ? ")
    public void copyMqttTableStructure() {
        try {

            LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
            String monthStr = DateUtil.formatLocalDateTime(nextMonth,"yyyyMM");
            // 新表名称
            String targetTable = TableNameConstant.MQTT_TABLE_NAME + "_" + monthStr;

            // 执行表结构复制
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s LIKE %s", targetTable, TableNameConstant.SH_MQTT_DEVICE_DATA_TEMPLATE);
            jdbcTemplate.execute(sql);

            System.out.println("表结构复制成功: " + targetTable + " 基于 " + TableNameConstant.SH_MQTT_DEVICE_DATA_TEMPLATE);
        } catch (Exception e) {
            System.err.println("表结构复制失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 每天都会执行,避免漏了,表存在额不会重复创建
     * 新表命名格式: sh_mqtt_device_recent_data_yyyyMM
     */
    @Scheduled(cron = "0 0 4 * * ? ")
    public void copyUdpTableStructure() {
        try {

            LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
            String monthStr = DateUtil.formatLocalDateTime(nextMonth,"yyyyMM");
            // 新表名称
            String targetTable = TableNameConstant.UDP_TABLE_NAME + "_" + monthStr;

            // 执行表结构复制
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s LIKE %s", targetTable, TableNameConstant.SH_UDP_DEVICE_DATA_TEMPLATE);
            jdbcTemplate.execute(sql);

            System.out.println("表结构复制成功: " + targetTable + " 基于 " + TableNameConstant.SH_UDP_DEVICE_DATA_TEMPLATE);
        } catch (Exception e) {
            System.err.println("表结构复制失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
