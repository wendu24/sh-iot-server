package com.ruoyi.business.iot.common.vo.uplink;

import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UdpCmd08DataVO {


    private String deviceSn;

    private Integer cmdCode ;
    /**
     * 设备版本
     */
    private BigDecimal deviceVersion;
    /**
     * 异常
     */
    private List<AbnormalTypeEnum> abnormalTypes;
    /**
     * 电池电量
     */
    private Integer batteryLevel;

    private Short reportPeriod ;

    private Integer signalStrength;

    private String iccId;

    private List<RoomDataVO> roomDataVOList;
}
