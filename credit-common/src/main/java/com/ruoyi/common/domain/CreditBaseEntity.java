package com.ruoyi.common.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreditBaseEntity {

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

}
