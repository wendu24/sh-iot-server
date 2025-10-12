package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.UdpDeviceDataTemplateDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.UdpDeviceDataTemplateService;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UdpHistoryDataObserver extends AbstractUplinkMsgObserver{

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UdpDeviceDataTemplateService udpDeviceDataTemplateService;

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

        List<UdpDeviceDataTemplateDO> saveList = new ArrayList<>();
        udpCmd08DataVO.getRoomDataVOList().forEach(roomDataVO -> {
            UdpDeviceDataTemplateDO udpDeviceRecentDataDO = new UdpDeviceDataTemplateDO();

            BeanUtil.copyProperties(udpCmd08DataVO,udpDeviceRecentDataDO );
            List<AbnormalTypeEnum> abnormalTypes = udpCmd08DataVO.getAbnormalTypes();
            if(CollectionUtils.isNotEmpty(abnormalTypes)){
                String abnormals = abnormalTypes.stream().map(AbnormalTypeEnum::getCode).map(String::valueOf).collect(Collectors.joining(","));
                udpDeviceRecentDataDO.setAbnormalTypes(abnormals);

            }
            udpDeviceRecentDataDO.setCommunityId(deviceDO.getCommunityId());
            udpDeviceRecentDataDO.setCommunityName(deviceDO.getCommunityName());
            udpDeviceRecentDataDO.setReportPeriod(udpCmd08DataVO.getReportPeriod().intValue());
            udpDeviceRecentDataDO.setCreateTime(LocalDateTime.now());
            udpDeviceRecentDataDO.setCollectTime(roomDataVO.getCollectTime());
            udpDeviceRecentDataDO.setRoomHumidity(roomDataVO.getRoomHumidity());
            udpDeviceRecentDataDO.setRoomTemperature(roomDataVO.getRoomTemperature());
            saveList.add(udpDeviceRecentDataDO);
            log.info("收到udp设备上报数据,开始准备保存到历史数据表={}",udpDeviceRecentDataDO.getDeviceSn());
        });
        udpDeviceDataTemplateService.saveBatch(saveList);
//            udpCmd08DataVO.getRoomDataVOList().sort(Comparator.comparing(RoomDataVO::getCollectTime).reversed());

    }
}
