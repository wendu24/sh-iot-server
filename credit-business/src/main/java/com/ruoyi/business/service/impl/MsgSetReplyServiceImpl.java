package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.mapper.MsgSetReplyMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.service.MsgSetReplyService;
import com.ruoyi.business.vo.MsgSetReplyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MsgSetReplyServiceImpl extends ServiceImpl<MsgSetReplyMapper, MsgSetReplyDO> implements MsgSetReplyService {


    @Override
    public Page<MsgSetReplyVO> list(MsgSetReplyVO msgSetReplyVO) {
        // 1. 构建查询条件
        LambdaQueryWrapper<MsgSetReplyDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(msgSetReplyVO.getDeviceSn()), MsgSetReplyDO::getDeviceSn, msgSetReplyVO.getDeviceSn())
                .eq(Objects.nonNull(msgSetReplyVO.getCmdCode()), MsgSetReplyDO::getCmdCode, msgSetReplyVO.getCmdCode())
                .eq(Objects.nonNull(msgSetReplyVO.getCommunityId()), MsgSetReplyDO::getCommunityId, msgSetReplyVO.getCommunityId())
                // 修正：原代码重复添加了 cmdCode 查询
                .ge(Objects.nonNull(msgSetReplyVO.getPublishStartTime()), MsgSetReplyDO::getPublishTime, msgSetReplyVO.getPublishStartTime())
                // 修正：结束时间应该用 le (less than or equal)
                .le(Objects.nonNull(msgSetReplyVO.getPublishEndTime()), MsgSetReplyDO::getPublishTime, msgSetReplyVO.getPublishEndTime())
                // 修正：处理 hasReply 的判断逻辑，确保其为布尔值
                .isNotNull(msgSetReplyVO.getHasReply() != null && msgSetReplyVO.getHasReply(), MsgSetReplyDO::getReplyBody)
                .isNull(msgSetReplyVO.getHasReply() != null && !msgSetReplyVO.getHasReply(), MsgSetReplyDO::getReplyBody)
                .orderByDesc(MsgSetReplyDO::getPublishTime);

        // 2. 执行分页查询
        Page<MsgSetReplyDO> pageParam = new Page<>(msgSetReplyVO.getPageNum(), msgSetReplyVO.getPageSize());
        Page<MsgSetReplyDO> doPage = page(pageParam, queryWrapper);

        // 3. DO -> VO 转换
        List<MsgSetReplyVO> voList = doPage.getRecords().stream().map(doRecord -> {
            MsgSetReplyVO vo = new MsgSetReplyVO();
            BeanUtil.copyProperties(doRecord, vo);

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode msgBodyNode = mapper.readTree(doRecord.getMsgBody());
                Integer readWriteFlag = msgBodyNode.get("readWriteFlag").asInt();
                vo.setReadOrWrite(readWriteFlag);

                if (ReadWriteEnum.READ.getCode().equals(readWriteFlag) && StringUtils.isNotBlank(doRecord.getReplyBody())) {
                    JsonNode replyBodyNode = mapper.readTree(doRecord.getReplyBody());
                    vo.setReplyData(replyBodyNode.get("data").asText());
                } else if (ReadWriteEnum.WRITE.getCode().equals(readWriteFlag)) {
                    String data = msgBodyNode.get("data").asText(null);
                    String dataStr = msgBodyNode.get("dataStr").asText(null);
                    vo.setPublishData(StringUtils.isEmpty(data) ? dataStr : data);
                }
            } catch (Exception e) {
               log.error("查询发布和回复的消息出错啦doRecord={}",JSONObject.toJSONString(doRecord),e);
            }

            return vo;
        }).collect(Collectors.toList());

        Page<MsgSetReplyVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(voList);

        return voPage;

    }


}
