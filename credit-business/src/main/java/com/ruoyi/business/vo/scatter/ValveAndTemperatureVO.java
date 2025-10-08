package com.ruoyi.business.vo.scatter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValveAndTemperatureVO implements Point2D{


    /**
     * 平均温度
     */
    private BigDecimal avgTemperature;


    /**
     * 平均阀门开度
     */
    private BigDecimal avgValvePosition;


    @Override
    public double getX() {
        return this.avgValvePosition.doubleValue();
    }

    @Override
    public double getY() {
        return avgTemperature.doubleValue();
    }
}
