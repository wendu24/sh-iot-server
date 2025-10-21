package com.ruoyi.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.business.validate.CreateGroup;
import com.ruoyi.business.validate.UpdateGroup;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;

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
     * 温度补偿模式 (00=无补偿, 01=超过【设定功率1】保持上传无负载时温度, 02=超过【设定功率1】启用温度升降的功率补偿)
     */
    private Integer temperatureCompensationMode;

    /**
     * 补偿设定时间1 (单位: 秒)
     */
    private Integer compensationTime1;

    /**
     * 补偿设定时间2 (单位: 秒)
     */
    private Integer compensationTime2;

    /**
     * 补偿设定时间3 (单位: 秒)
     */
    private Integer compensationTime3;

    /**
     * 补偿设定功率1 (单位: 瓦特, 精度: 小数点后1位)
     */
    private BigDecimal compensationWatt1; // 使用 BigDecimal 处理 decimal 类型

    /**
     * 供热模式有效性标志 (0=无效, 1=有效)
     */
    private Integer modeValidFlag;


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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Tolerate
    public DeviceVO() {
    }
}
