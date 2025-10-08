package com.ruoyi.business.job;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ruoyi.business.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Component
public class TableScheduler {

    // 假设您有一个名为 MyDataMapper 的 Mapper，用于执行操作
    // 注意：这里的具体Mapper不重要，只要是任意一个BaseMapper即可获取SqlSession
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    // 定义需要复制的基础表名
    private static final String SH_MQTT_DEVICE_RECENT_DATA = "sh_mqtt_device_recent_data";

    /**
     * 每月最后一天 23:59:00 执行
     * Cron表达式含义: 秒 分 时 日 月 周
     * "0 59 23 L * ?" -> 每月最后一天 23点59分0秒
     * * 注意：实际生产环境建议将时间调整到凌晨非业务高峰期，例如 "0 0 1 L * ?" (每月最后一天 1点0分0秒)
     */
    @Scheduled(cron = "0 59 23 L * ?")
    public void createNextMonthTable() {
        log.info("--- 开始执行每月表结构复制任务 ---");

        // 1. 计算下个月的表名后缀
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfNextMonth = today.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

        // 格式化为 YYYYMM (例如: 202409)
        String nextMonthSuffix = firstDayOfNextMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String newTableName = SH_MQTT_DEVICE_RECENT_DATA + "_" + nextMonthSuffix;

        // 2. 构造 DDL 语句
        // 使用 IF NOT EXISTS 防止表已存在时报错
        String sql = String.format("CREATE TABLE IF NOT EXISTS `%s` LIKE `%s`",
                newTableName,
                SH_MQTT_DEVICE_RECENT_DATA);

        log.info("准备创建新表: {}", newTableName);
        log.info("执行 SQL: {}", sql);

        // 3. 执行 DDL 语句
        try {
            // 通过 SqlSession 执行原生 SQL
            sqlSessionTemplate.update("com.baomidou.mybatisplus.core.mapper.BaseMapper.ddlExecutor", sql);
            log.info("表结构 {} 复制成功!", newTableName);
        } catch (Exception e) {
            log.error("表结构复制失败，新表名: {}", newTableName, e);
        }

        log.info("--- 每月表结构复制任务执行结束 ---");
    }


}
