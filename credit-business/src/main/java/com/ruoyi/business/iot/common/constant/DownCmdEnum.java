package com.ruoyi.business.iot.common.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * CMD 命令枚举
 */
@Getter
public enum DownCmdEnum {



    DOWNLINK_16((byte)0x16, String.class,50,"下发修改/读取目标地址"),
    DOWNLINK_19((byte)0x19, String.class,16,"下发修改/读取AES密钥"),
    DOWNLINK_26((byte)0x26, String.class,16,"下发修改/读取鉴权密钥"),

    DOWNLINK_23((byte)0x23, Float.class,2,"下发设置/读取上报间隔"),
    DOWNLINK_25((byte)0x25,Float.class,2,"下发设置/读取采集间隔"),
    DOWNLINK_30((byte)0x30,Float.class,2,"下发设置/读取阀门开度"),

    DOWNLINK_40((byte)0x40,null,null,"下发读取实时数据并上报"),
    DOWNLINK_F0((byte)0xF0,null,null,"设备固件升级"),
    DOWNLINK_FF((byte)0xFF, Date.class,4,"下发时间戳"),
    DOWNLINK_UDP_RESPONSE((byte)0x08, Date.class,4,"下发时间戳"),

    /**
     * udp 1.1版本命令
     */
    DOWNLINK_UDP_E0((byte)0xE0, Byte.class,1,"下发温度补偿模式"),
    DOWNLINK_UDP_E1((byte)0xE1, Float.class,2,"补偿设定时间1"),
    DOWNLINK_UDP_E2((byte)0xE2, Float.class,2,"补偿设定时间2"),
    DOWNLINK_UDP_E3((byte)0xE3, Float.class,2,"补偿设定时间3"),

    DOWNLINK_UDP_E4((byte)0xE4, Float.class,2,"补偿设定功率1"),
    DOWNLINK_UDP_E5((byte)0xE5, Byte.class,1,"供热模式有效性"),



    ;


    public static List<DownCmdEnum> mqttAutoFreshCommands(){
        return Arrays.asList(DOWNLINK_16,DOWNLINK_19,DOWNLINK_26,DOWNLINK_23,DOWNLINK_25,DOWNLINK_30);
    }


    public static List<DownCmdEnum> udpAutoFreshCommands(){
        return Arrays.asList(DOWNLINK_16,DOWNLINK_19,DOWNLINK_26,DOWNLINK_23,DOWNLINK_25,DOWNLINK_UDP_E0,DOWNLINK_UDP_E1,DOWNLINK_UDP_E2,DOWNLINK_UDP_E3,DOWNLINK_UDP_E4,DOWNLINK_UDP_E5 );
    }


    public static DownCmdEnum getByCode(byte code){
        return Arrays.stream(DownCmdEnum.values()).filter(cmdEnum -> code == cmdEnum.getCode()).findAny().orElseThrow(()->new RuntimeException("无效的CMD" + code ));
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



    DownCmdEnum(Byte code, Class dataClazz, Integer dataLength, String desc) {
        this.code = code;
        DataClazz = dataClazz;
        this.dataLength = dataLength;
        this.desc = desc;
    }
}
