package com.ruoyi.business.iot.protocol.constant;

import lombok.Getter;

@Getter
public enum ReadWriteEnum {

    READ( 0x00,"读"),

    WRITE( 0x01,"写"),
    ;

    private Integer code;

    private String desc;

    ReadWriteEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
