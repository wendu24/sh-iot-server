package com.ruoyi.business.constant;

import lombok.Getter;

@Getter
public enum DeleteEnum {

    DELETED(-1,"已删除"),


    NORMAL(1,"正常"),

    ;

    private Integer code;

    private String desc;

    DeleteEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
