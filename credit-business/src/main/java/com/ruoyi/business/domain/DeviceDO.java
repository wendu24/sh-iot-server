package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
}
