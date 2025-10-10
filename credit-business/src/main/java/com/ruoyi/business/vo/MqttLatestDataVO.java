package com.ruoyi.business.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MqttLatestDataVO {


    /**
     * 设备序列号
     */
    private String deviceSn;


    private Long communityId;

    private String communityName;

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
     * 供水压力
     */
    private BigDecimal supplyWaterPressure;

    /**
     * 回水压力
     */
    private BigDecimal returnWaterPressure;
}
