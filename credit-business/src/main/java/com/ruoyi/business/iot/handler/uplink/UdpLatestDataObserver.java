package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.UdpDeviceLatestDataDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.RoomDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.service.UdpDeviceLatestDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UdpLatestDataObserver extends AbstractUplinkMsgObserver{

    @Autowired
    private UdpDeviceLatestDataService udpDeviceLatestDataService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        UdpCmd08DataVO udpCmd08DataVO = uplinkDataVO.getUdpCmd08DataVO();
        if(Objects.isNull(udpCmd08DataVO))
            return;

        LambdaQueryWrapper<UdpDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UdpDeviceLatestDataDO::getDeviceSn, udpCmd08DataVO.getDeviceSn());
        UdpDeviceLatestDataDO dbData = udpDeviceLatestDataService.getOne(queryWrapper);


        udpCmd08DataVO.getRoomDataVOList().sort(Comparator.comparing(RoomDataVO::getCollectTime).reversed());
        RoomDataVO roomDataVO = udpCmd08DataVO.getRoomDataVOList().get(0);

        UdpDeviceLatestDataDO udpDeviceLatestDataDO = new UdpDeviceLatestDataDO();
        BeanUtil.copyProperties(udpCmd08DataVO,udpDeviceLatestDataDO );
        List<AbnormalTypeEnum> abnormalTypes = udpCmd08DataVO.getAbnormalTypes();
        if(CollectionUtils.isNotEmpty(abnormalTypes)){
            String abnormals = abnormalTypes.stream().map(AbnormalTypeEnum::getCode).map(String::valueOf).collect(Collectors.joining(","));
            udpDeviceLatestDataDO.setAbnormalTypes(abnormals);
        }
        udpDeviceLatestDataDO.setReportPeriod(udpCmd08DataVO.getReportPeriod().intValue());
        udpDeviceLatestDataDO.setCreateTime(LocalDateTime.now());
        udpDeviceLatestDataDO.setCollectTime(roomDataVO.getCollectTime());
        udpDeviceLatestDataDO.setRoomHumidity(roomDataVO.getRoomHumidity());
        udpDeviceLatestDataDO.setRoomTemperature(roomDataVO.getRoomTemperature());
        if(Objects.isNull(dbData)){
            udpDeviceLatestDataService.save(udpDeviceLatestDataDO);
        }else {
            udpDeviceLatestDataDO.setId(dbData.getId());
            udpDeviceLatestDataService.updateById(udpDeviceLatestDataDO);
        }


    }
}
