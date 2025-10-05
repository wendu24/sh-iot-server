package com.ruoyi.business.vo.home;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class OverviewVO {

    private Integer communityNum;
    /**
     * 设备数
     */
    private Map<Integer,Integer> deviceTypeNum;
    /**
     * 异常数
     */
    private Map<Integer,Integer> abnormalTypeNum;
    /**
     * 平均室温
     */
    private BigDecimal avgRoomTemperature;
    /**
     * 平均设备开度
     */
    private BigDecimal avgValvePosition;


}
