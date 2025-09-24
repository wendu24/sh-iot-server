package com.ruoyi.business.iot.common.vo.uplink;

import com.ruoyi.business.iot.common.constant.CmdEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CMD:FF上传数据 reply讯息
 */
@Data
@Builder
public class UplinkCmdFFDataVO {

    private String deviceSn;

    private byte cmdCode;
    /**
     * 下发数据时的mid
     */
    private Short mid;
    /**
     * 00 读, 01写, 02应答
     */
    private Byte readWriteFlag;
    /**
     * 设备时间
     */
    private LocalDateTime deviceTime;
    /**
     * 0 成功; 1失败 , 下发写命令有该值, 这两个互斥
     */
    private Byte result;
    /**
     * 如果是我读数据,那么就有该值
     */
    private byte[] dataBytes;
}
