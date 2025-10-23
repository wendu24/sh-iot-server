package com.ruoyi.business.constant;


import lombok.Data;
import lombok.Getter;

/**
 * 10:DTU;20:阀门;30:测温模板;40:压力传感器
 */
@Getter
public enum DeviceTypeEnum {

    DEV_DTU(10,"DTU设备"),
    DEV_VALVE(20,"阀门"),
    DEV_TEMPERATURE(30,"测温模板"),
    DEV_PRESSURE(40,"压力传感器"),


    ;


    public static boolean udpDeviceType(Integer deviceType){
        return DEV_TEMPERATURE.code.equals(deviceType);
    }

    private Integer code;

    private String desc;

    DeviceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
