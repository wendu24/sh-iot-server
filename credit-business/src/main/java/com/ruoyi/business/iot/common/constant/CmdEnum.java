package com.ruoyi.business.iot.common.constant;

import lombok.Getter;

/**
 * CMD 命令枚举
 */
@Getter
public enum CmdEnum {

    UPLINK_08((byte)0x08,"上传数据"),
    UPLINK_FF((byte)0x00,"应答数据"),

    ;
    private Byte code;

    private String desc;




    CmdEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
