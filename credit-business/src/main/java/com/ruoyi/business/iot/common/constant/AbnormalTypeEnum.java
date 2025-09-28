package com.ruoyi.business.iot.common.constant;

import lombok.Getter;

@Getter
public enum AbnormalTypeEnum {

    DISASSEMBLY(10,"拆卸告警"),

    VALVE_STALL(20,"阀门堵转"),

    SENSOR_ABNORMALITY(30,"传感器异常"),

    KEY_PRESS(40,"按键报警触发"),

    ;

    private Integer code;

    private String desc;

    AbnormalTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
