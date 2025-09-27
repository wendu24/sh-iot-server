package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sh_community")
public class CommunityDO {


    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 管理员
     */
    private Character manager;

    /**
     * 电话
     */
    private Character phone;

    /**
     * 位置（如经纬度）
     */
    private String location;

    /**
     * 删除标志 (1: 未删除, -1: 已删除)
     */
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
