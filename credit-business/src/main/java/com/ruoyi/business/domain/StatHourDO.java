package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sh_stat_hour")
public class StatHourDO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 社区ID
     */
    private Long communityId;

    /**
     * 社区名称
     */
    private String communityName;

    /**
     * 统计日期（格式：YYYY-MM-DD）
     */
    private LocalDate statDay;

    /**
     * 统计小时（格式：HH）
     */
    private Integer statHour;

    /**
     * 平均温度
     */
    private BigDecimal avgTemperature;

    /**
     * 平均湿度
     */
    private BigDecimal avgHumidity;

    /**
     * 平均回水温度
     */
    private BigDecimal avgReturnWaterTemperature;

    /**
     * 平均供水温度
     */
    private BigDecimal avgSupplyWaterTemperature;

    /**
     * 平均供水压力
     */
    private BigDecimal avgSupplyWaterPressure;

    /**
     * 平均回水压力
     */
    private BigDecimal avgReturnWaterPressure;

    /**
     * 平均阀门开度
     */
    private BigDecimal avgValvePosition;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    public String mapKey(){
        return communityId + "_" + statDay + "_" + statHour;
    }

}
