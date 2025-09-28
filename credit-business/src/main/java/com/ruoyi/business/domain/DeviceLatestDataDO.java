package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sh_device_latest_data")
public class DeviceLatestDataDO {


    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备序列号
     */
    private String deviceSn;

    /**
     * 数据采集时间
     */
    private LocalDateTime collectionTime;

    /**
     * 电池电量
     */
    private Integer batteryLevel;

    /**
     * 异常类型 (10:拆卸告警, 20:阀门堵转, 30:传感器异常) 逗号分割
     */
    private String abnormalTypes;

    /**
     * 数据上报周期，单位：分钟
     */
    private Integer uplinkPeriod;

    /**
     * 阀门开度，例如 60.2%
     */
    private BigDecimal valvePosition;

    /**
     * 阀门期望开度
     */
    private BigDecimal targetValvePosition;

    /**
     * 回水温度
     */
    private BigDecimal returnWaterTemperature;

    /**
     * 目标回水温度
     */
    private BigDecimal targetReturnWaterTemperature;

    /**
     * 供水温度
     */
    private BigDecimal supplyWaterTemperature;

    /**
     * 瞬时流量，单位：m³/h
     */
    private BigDecimal flowRate;

    /**
     * 瞬时热量
     */
    private BigDecimal heatOutput;

    /**
     * 累计流量
     */
    private BigDecimal totalFlowVolume;

    /**
     * 累计热量
     */
    private BigDecimal totalHeatOutput;

    /**
     * 供水压力
     */
    private BigDecimal supplyWaterPressure;

    /**
     * 回水压力
     */
    private BigDecimal returnWaterPressure;

    /**
     * 室内温度
     */
    private BigDecimal roomTemperature;

    /**
     * 目标室内温度
     */
    private BigDecimal targetRoomTemperature;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
