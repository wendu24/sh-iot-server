package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.domain.UdpDeviceLatestDataDO;
import com.ruoyi.business.mapper.MsgSetReplyMapper;
import com.ruoyi.business.mapper.UdpDeviceLatestDataMapper;
import com.ruoyi.business.service.UdpDeviceLatestDataService;
import com.ruoyi.business.vo.UdpLatestDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UdpDeviceLatestDataServiceImpl extends ServiceImpl<UdpDeviceLatestDataMapper, UdpDeviceLatestDataDO> implements UdpDeviceLatestDataService {


    public UdpLatestDataVO findByDeviceSN(String deviceSn){

        LambdaQueryWrapper<UdpDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UdpDeviceLatestDataDO::getDeviceSn,deviceSn);
        UdpDeviceLatestDataDO latestDataDO = getOne(queryWrapper);
        if(Objects.isNull(latestDataDO))
            return UdpLatestDataVO.builder().build();

        UdpLatestDataVO udpLatestDataVO = UdpLatestDataVO.builder().build();
        BeanUtil.copyProperties(latestDataDO,udpLatestDataVO);
        return udpLatestDataVO;

    }

}
