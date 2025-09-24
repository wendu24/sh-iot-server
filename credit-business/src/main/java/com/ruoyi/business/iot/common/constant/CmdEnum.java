package com.ruoyi.business.iot.common.constant;

import lombok.Getter;

/**
 * CMD 命令枚举
 */
@Getter
public enum CmdEnum {

    UPLINK_08((byte)0x08,"上传数据"),
    UPLINK_FF((byte)0x00,"应答数据"),

    DOWNLINK_16((byte)0x16,"下发修改/读取目标地址"),
    DOWNLINK_19((byte)0x19,"下发修改/读取AES密钥"),
    DOWNLINK_26((byte)0x26,"下发修改/读取鉴权密钥"),
    DOWNLINK_23((byte)0x23,"下发设置/读取上报间隔"),
    DOWNLINK_25((byte)0x25,"下发设置/读取采集间隔"),
    DOWNLINK_30((byte)0x30,"下发设置/读取阀门开度"),
    DOWNLINK_40((byte)0x40,"下发读取实时数据并上报"),
    DOWNLINK_F0((byte)0xF0,"设备固件升级"),



    ;
    private Byte code;

    private String desc;




    CmdEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
