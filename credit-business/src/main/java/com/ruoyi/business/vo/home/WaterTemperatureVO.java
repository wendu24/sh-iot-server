package com.ruoyi.business.vo.home;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WaterTemperatureVO {


    private Integer hour;

    private BigDecimal avgSupplyWaterTemperature;


    private BigDecimal avgReturnWaterTemperature;

}
