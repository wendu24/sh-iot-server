package com.ruoyi.business.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommunityVO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    private String address;

    /**
     * 管理员
     */
    @NotBlank(message = "管理员不能为空")
    private String manager;

    /**
     * 电话
     */
    @NotBlank(message = "电话不能为空")
    private String phone;

    /**
     * 位置（如经纬度）
     */
    private String location;
}
