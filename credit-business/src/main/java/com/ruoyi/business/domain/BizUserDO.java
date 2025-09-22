package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("biz_user")
public class BizUserDO {

    private Long id;

    private String userName;

    private String nickName;

    private String password;

    private String phoneNumber;

    private Integer status;

    private Integer delFlag;
}
