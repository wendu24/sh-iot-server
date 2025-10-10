package com.ruoyi.business.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentDataQueryVO {

    /**
     * 小区id
     */
    private String communityId;
    /**
     * 设备编号
     */
    private String deviceSn;
    /**
     * 数据采集时间
     */
    private LocalDateTime collectTimeStartTime;

    private LocalDateTime collectTimeEndTime;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;


}
