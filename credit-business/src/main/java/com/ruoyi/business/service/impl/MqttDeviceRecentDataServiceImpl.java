package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.mapper.MqttDeviceRecentDataMapper;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import com.ruoyi.business.vo.RecentDataQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class MqttDeviceRecentDataServiceImpl extends ServiceImpl<MqttDeviceRecentDataMapper, MqttDeviceRecentDataDO> implements MqttDeviceRecentDataService {



    @Override
    public Page<MqttDeviceRecentDataDO> list(RecentDataQueryVO recentDataQueryVO){

        LambdaQueryWrapper<MqttDeviceRecentDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(recentDataQueryVO.getDeviceSn()),MqttDeviceRecentDataDO::getDeviceSn,recentDataQueryVO.getDeviceSn());
        queryWrapper.eq(Objects.nonNull(recentDataQueryVO.getCommunityId()),MqttDeviceRecentDataDO::getCommunityId,recentDataQueryVO.getCommunityId());
        queryWrapper.ge(Objects.nonNull(recentDataQueryVO.getCollectStartTime()),MqttDeviceRecentDataDO::getCollectionTime,recentDataQueryVO.getCollectStartTime());
        queryWrapper.le(Objects.nonNull(recentDataQueryVO.getCollectEndTime()),MqttDeviceRecentDataDO::getCollectionTime,recentDataQueryVO.getCollectEndTime());
        queryWrapper.orderByDesc(MqttDeviceRecentDataDO::getId);
        Page<MqttDeviceRecentDataDO> pageParam = new Page<>(recentDataQueryVO.getPageNum(), recentDataQueryVO.getPageSize());
        return  page(pageParam, queryWrapper);
    }
}
