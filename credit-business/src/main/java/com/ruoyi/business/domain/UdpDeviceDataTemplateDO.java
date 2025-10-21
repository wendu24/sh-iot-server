package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sh_udp_device_data_template")
public class UdpDeviceDataTemplateDO {


    private Long id;

    private String deviceSn;


    private Long communityId;

    private String communityName;

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

    private Integer reportPeriod ;

    private Integer signalStrength;

    private String iccId;

    private LocalDateTime collectTime ;
    /**
     * 室内温度
     */
    private BigDecimal roomTemperature ;

    // 室内湿度
    private BigDecimal roomHumidity ;
    /**
     * 功率
     */
    private BigDecimal watt ;

    private LocalDateTime createTime;
}
