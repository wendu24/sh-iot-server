package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.mapper.MsgSetReplyMapper;
import com.ruoyi.business.mapper.UdpDeviceRecentDataMapper;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
import com.ruoyi.business.vo.RecentDataQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UdpDeviceRecentDataServiceImpl extends ServiceImpl<UdpDeviceRecentDataMapper, UdpDeviceRecentDataDO> implements UdpDeviceRecentDataService {



    @Override
    public Page<UdpDeviceRecentDataDO> list(RecentDataQueryVO recentDataQueryVO){

        LambdaQueryWrapper<UdpDeviceRecentDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(recentDataQueryVO.getDeviceSn()), UdpDeviceRecentDataDO::getDeviceSn,recentDataQueryVO.getDeviceSn());
        queryWrapper.eq(Objects.nonNull(recentDataQueryVO.getCommunityId()),UdpDeviceRecentDataDO::getCommunityId,recentDataQueryVO.getCommunityId());
        queryWrapper.ge(Objects.nonNull(recentDataQueryVO.getCollectTimeStartTime()),UdpDeviceRecentDataDO::getCollectTime,recentDataQueryVO.getCollectTimeStartTime());
        queryWrapper.le(Objects.nonNull(recentDataQueryVO.getCollectTimeEndTime()),UdpDeviceRecentDataDO::getCollectTime,recentDataQueryVO.getCollectTimeEndTime());
        queryWrapper.orderByDesc(UdpDeviceRecentDataDO::getId);
        Page<UdpDeviceRecentDataDO> pageParam = new Page<>(recentDataQueryVO.getPageNum(), recentDataQueryVO.getPageSize());
        return  page(pageParam, queryWrapper);
    }
}
