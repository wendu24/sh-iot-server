package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.mapper.MsgSetReplyMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.service.MsgSetReplyService;
import com.ruoyi.business.vo.MsgSetReplyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class MsgSetReplyServiceImpl extends ServiceImpl<MsgSetReplyMapper, MsgSetReplyDO> implements MsgSetReplyService {


    @Override
    public Page<MsgSetReplyDO> list(MsgSetReplyVO msgSetReplyVO){

        LambdaQueryWrapper<MsgSetReplyDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(msgSetReplyVO.getDeviceSn()),MsgSetReplyDO::getDeviceSn,msgSetReplyVO.getDeviceSn());
        queryWrapper.eq(Objects.nonNull(msgSetReplyVO.getCmdCode()),MsgSetReplyDO::getCmdCode,msgSetReplyVO.getCmdCode());
        queryWrapper.eq(Objects.nonNull(msgSetReplyVO.getCmdCode()),MsgSetReplyDO::getCmdCode,msgSetReplyVO.getCmdCode());
        queryWrapper.ge(Objects.nonNull(msgSetReplyVO.getPublishStartTime()),MsgSetReplyDO::getPublishTime,msgSetReplyVO.getPublishStartTime());
        queryWrapper.ge(Objects.nonNull(msgSetReplyVO.getPublishEndTime()),MsgSetReplyDO::getPublishTime,msgSetReplyVO.getPublishEndTime());
        queryWrapper.isNotNull(msgSetReplyVO.getHasReply(),MsgSetReplyDO::getReplyBody);
        queryWrapper.isNull(!msgSetReplyVO.getHasReply(),MsgSetReplyDO::getReplyBody);

        Page<MsgSetReplyDO> pageParam = new Page<>(msgSetReplyVO.getPageNum(), msgSetReplyVO.getPageSize());
        return page(pageParam,queryWrapper);

    }


}
