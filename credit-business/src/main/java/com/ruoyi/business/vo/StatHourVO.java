package com.ruoyi.business.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatHourVO {



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
}
