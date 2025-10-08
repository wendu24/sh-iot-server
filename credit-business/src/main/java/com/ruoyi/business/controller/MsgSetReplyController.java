package com.ruoyi.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.service.MsgSetReplyService;
import com.ruoyi.business.vo.MsgSetReplyVO;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/biz/msg/set-reply")
public class MsgSetReplyController {

    @Autowired
    MsgSetReplyService msgSetReplyService;

    @RequestMapping("/list")
    public Page<MsgSetReplyDO> list(@RequestBody MsgSetReplyVO msgSetReplyVO){
       return list(msgSetReplyVO);
    }


}
