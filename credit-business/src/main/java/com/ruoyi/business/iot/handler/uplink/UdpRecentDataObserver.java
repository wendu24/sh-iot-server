package com.ruoyi.business.iot.handler.uplink;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.RoomDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
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
public class UdpRecentDataObserver extends AbstractUplinkMsgObserver{

    @Autowired
    private UdpDeviceRecentDataService udpDeviceRecentDataService;

    @Autowired
    DeviceService deviceService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        UdpCmd08DataVO udpCmd08DataVO = uplinkDataVO.getUdpCmd08DataVO();
        if(Objects.isNull(udpCmd08DataVO))
            return;
        String deviceSn = udpCmd08DataVO.getDeviceSn();
        DeviceDO deviceDO;
        try {
            deviceDO = deviceService.findByDeviceSn(deviceSn);
        } catch (Exception e) {
            log.error("未找到deviceSn={]",deviceSn,e);
            return;
        }

        List<UdpDeviceRecentDataDO> saveList = new ArrayList<>();
        udpCmd08DataVO.getRoomDataVOList().forEach(roomDataVO -> {
            UdpDeviceRecentDataDO udpDeviceRecentDataDO = new UdpDeviceRecentDataDO();

            BeanUtil.copyProperties(udpCmd08DataVO,udpDeviceRecentDataDO );
            List<AbnormalTypeEnum> abnormalTypes = udpCmd08DataVO.getAbnormalTypes();
            if(CollectionUtils.isNotEmpty(abnormalTypes)){
                String abnormals = abnormalTypes.stream().map(AbnormalTypeEnum::getCode).map(String::valueOf).collect(Collectors.joining(","));
                udpDeviceRecentDataDO.setAbnormalTypes(abnormals);

            }
            udpDeviceRecentDataDO.setReportPeriod(udpCmd08DataVO.getReportPeriod().intValue());
            udpDeviceRecentDataDO.setCreateTime(LocalDateTime.now());
            udpDeviceRecentDataDO.setCollectTime(roomDataVO.getCollectTime());
            udpDeviceRecentDataDO.setRoomHumidity(roomDataVO.getRoomHumidity());
            udpDeviceRecentDataDO.setCommunityId(deviceDO.getCommunityId());
            udpDeviceRecentDataDO.setCommunityName(deviceDO.getCommunityName());
            udpDeviceRecentDataDO.setRoomTemperature(roomDataVO.getRoomTemperature());
            saveList.add(udpDeviceRecentDataDO);
        });
        udpDeviceRecentDataService.saveBatch(saveList);
        log.info("保存udp数据成功");

//            udpCmd08DataVO.getRoomDataVOList().sort(Comparator.comparing(RoomDataVO::getCollectTime).reversed());


    }
}
