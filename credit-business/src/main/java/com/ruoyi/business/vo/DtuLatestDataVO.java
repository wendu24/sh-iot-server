package com.ruoyi.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DtuLatestDataVO {


    /**
     * 设备序列号
     */
    private String deviceSn;


    /**
     * 数据采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectionTime;

    /**
     * 电池电量
     */
    private Integer batteryLevel;

    /**
     * 信号强度
     */
    private Integer signalStrength;



}
