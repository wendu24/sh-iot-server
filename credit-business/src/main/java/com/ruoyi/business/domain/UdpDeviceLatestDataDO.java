package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.uplink.RoomDataVO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sh_udp_device_latest_data")
public class UdpDeviceLatestDataDO {

    private Long id;

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

    private LocalDateTime createTime;
}
