package com.ruoyi.business.vo;

import com.ruoyi.business.validate.CreateGroup;
import com.ruoyi.business.validate.UpdateGroup;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DeviceVO {


    /**
     * 主键ID
     */
    @NotNull(groups = { UpdateGroup.class}, message = "id不能为空")
    private Long id;

    /**
     * 设备序列号
     */
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class},message = "SN不能为空")
    private String deviceSn;
    /**
     * 设备位置
     */
    private String position;

    /**
     * 设备类型
     * @see com.ruoyi.business.constant.DeviceTypeEnum
     */
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class},message = "设备类型不能为空")
    private Integer deviceType;

    /**
     * DTU序列号
     */
    private String dtuSn;

    /**
     * 小区ID
     */
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class},message = "小区不能为空")
    private Long communityId;

    /**
     * 小区名称
     */
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class},message = "小区不能为空")
    private String communityName;


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

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
