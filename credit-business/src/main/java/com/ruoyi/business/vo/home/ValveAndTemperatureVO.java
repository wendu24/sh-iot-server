package com.ruoyi.business.vo.home;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ValveAndTemperatureVO {

    /**
     * 阀门开度
     */
    private BigDecimal valve;

    /**
     * 室温
     */
    private BigDecimal temperature;


}
