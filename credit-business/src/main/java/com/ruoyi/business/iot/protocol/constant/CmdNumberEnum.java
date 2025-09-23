package com.ruoyi.business.iot.protocol.constant;

import lombok.Getter;

/**
 * CMD 命令枚举
 */
@Getter
public enum CmdNumberEnum {
    ;
    private Integer code;

    private String desc;




    CmdNumberEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
