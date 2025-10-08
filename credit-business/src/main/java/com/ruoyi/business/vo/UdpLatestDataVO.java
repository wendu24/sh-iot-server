package com.ruoyi.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UdpLatestDataVO {


    private String deviceSn;


    /**
     * 设备版本
     */
    private BigDecimal deviceVersion;
    /**
     * 异常
     */
    private String abnormalTypes;

    /**
     * 电池电量
     */
    private Integer batteryLevel;

    /**
     * 上报周期
     */
    private Integer reportPeriod ;

    /**
     * 信号强度
     */
    private Integer signalStrength;


    private String iccId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectTime ;
    /**
     * 室内温度
     */
    private BigDecimal roomTemperature ;

    // 室内湿度
    private BigDecimal roomHumidity ;

}
