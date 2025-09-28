package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sh_device")
public class DeviceDO {


    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备序列号
     */
    private String deviceSn;

    /**
     * 设备类型
     * @see com.ruoyi.business.constant.DeviceTypeEnum
     */
    private Integer deviceType;

    /**
     * DTU序列号
     */
    private String dtuSn;

    /**
     * 小区ID
     */
    private Long communityId;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志 (1: 未删除, -1: 已删除)
     * @see com.ruoyi.business.constant.DeleteEnum
     */
    private Integer deleteFlag;






    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 加密密钥
     */
    private String aesKey;

    /**
     * 鉴权密钥
     */
    private String loginKey;

    /**
     * 数据上报周期，单位：分钟
     */
    private Integer reportPeriod;

    /**
     * 数据采集间隔，单位：分钟
     */
    private Integer collectPeriod;

    /**
     * 阀门开度
     */
    private BigDecimal valvePosition;

    /**
     * 目标回水温度
     */
    private BigDecimal returnWaterTemperature;

    /**
     * 目标室温
     */
    private BigDecimal roomTemperature;


}
