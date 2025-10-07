package com.ruoyi.business.vo;

import com.ruoyi.business.validate.CreateGroup;
import com.ruoyi.business.validate.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommunityVO {

    /**
     * 主键ID
     */
    @NotNull(groups = { UpdateGroup.class}, message = "id不能为空")
    private Long id;
    /**
     * 名称
     */
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "名称不能为空")
    private String name;

    /**
     * 地址
     */
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class},message = "地址不能为空")
    private String address;

    /**
     * 管理员
     */
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class},message = "管理员不能为空")
    private String manager;

    /**
     * 电话
     */
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class},message = "电话不能为空")
    private String phone;

    /**
     * 位置（如经纬度）
     */
    private String location;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;
}
