package com.ruoyi.business.iot.common.vo.uplink;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RoomDataVO {

    private LocalDateTime collectTime ;
    /**
     * 室内温度
     */
    private BigDecimal roomTemperature ;
    // 室内湿度
    private BigDecimal roomHumidity ;

    // 功率
    private BigDecimal watt ;


}
