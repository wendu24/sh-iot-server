package com.ruoyi.business.vo;

import lombok.Data;

@Data
public class RecentDataQueryVO {

    private String communityId;

    private String deviceSn;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;


}
