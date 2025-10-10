package com.ruoyi.business.vo.home;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RoomDataThirtyDayVO {

    private Integer hour;

    private BigDecimal avgRoomTemperature;

    private BigDecimal avgRoomHumidity;

    private BigDecimal supplyWaterTemperature;

    private BigDecimal supplyWaterPressure;

    private String communityName;

}
