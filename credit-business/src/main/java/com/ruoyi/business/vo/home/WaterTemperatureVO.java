package com.ruoyi.business.vo.home;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WaterTemperatureVO {


    private Integer hour;

    private BigDecimal agvSupplyWaterTemperature;


    private BigDecimal agvReturnWaterTemperature;

}
