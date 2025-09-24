package com.ruoyi.business.iot.common.vo.uplink;

import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
public class UplinkCmd08DataVO {


    private String deviceSn;

    /**
     * 电池电量
     */
    private Integer batteryLevel;

    private List<AbnormalTypeEnum> abnormalTypes;
    /**
     * 数据上报周期，10分钟
     */
    private Short uplinkPeriod;
    /**
     * 采集时间
     */
    private LocalDateTime collectionTime;

    /**
     * 阀门开度 60.2%
     */
    private BigDecimal valvePosition;

    /**
     * 期望开度
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
     * 瞬时流量 m³
     */
    private BigDecimal flowRate;

    /**
     * 瞬时热量 kW
     */
    private BigDecimal heatOutput;

    /**
     * 累计流量 m³
     */
    private BigDecimal totalFlowVolume;

    /**
     * 累计热量 kW
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

    private BigDecimal targetRoomTemperature;







}
