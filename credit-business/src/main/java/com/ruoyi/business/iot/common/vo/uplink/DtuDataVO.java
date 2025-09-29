package com.ruoyi.business.iot.common.vo.uplink;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DtuDataVO {

    /**
     * 电池电量
     */
    private Integer batteryLevel;
    /**
     * 信号强度
     */
    private Integer signalStrength;

    /**
     * 11个字节的 物联网卡ICCID
     */
    private String iccID;

    /**
     * 是否需要平台应答
     */
    private Integer replyFlag;
    /**
     * 指令条数
     */
    private Integer cmdNum;
}
