package com.ruoyi.business.vo.home;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class OverviewVO {

    private Integer communityNum;
    /**
     * 设备数
     */
    private Map<Integer,Long> deviceTypeNum;
    /**
     * 异常数
     */
    private Map<String,Long> abnormalTypeNum;
    /**
     * 平均室温
     */
    private BigDecimal avgRoomTemperature;
    /**
     * 平均湿度
     */
    private BigDecimal avgRoomHumidity;
    /**
     * 平均设备开度
     */
    private BigDecimal avgValvePosition;


}
