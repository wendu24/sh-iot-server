package com.ruoyi.business.iot.common.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Date;

@Getter
public enum UplinkCmdEnum {

    UPLINK_08((byte)0x08,null,null,"上传数据"),
    UPLINK_FF((byte)0x00,null,null,"应答数据"),

    ;


    public static UplinkCmdEnum getByCode(byte code){
        return Arrays.stream(UplinkCmdEnum.values()).filter(cmdEnum -> code == cmdEnum.getCode()).findAny().orElseThrow(()->new RuntimeException("无效的CMD" + code ));
    }

    /**
     * 命令字编码
     */
    private Byte code;
    /**
     * 如果是下发数据,下发数据的类型
     */
    private Class DataClazz;
    /**
     * 下发数据的字节数
     */
    private Integer dataLength;
    /**
     * 描述
     */
    private String desc;



    UplinkCmdEnum(Byte code, Class dataClazz, Integer dataLength, String desc) {
        this.code = code;
        DataClazz = dataClazz;
        this.dataLength = dataLength;
        this.desc = desc;
    }
}
