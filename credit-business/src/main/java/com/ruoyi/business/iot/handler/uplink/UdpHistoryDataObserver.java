package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.UdpDeviceDataTemplateDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.service.UdpDeviceDataTemplateService;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UdpHistoryDataObserver extends AbstractUplinkMsgObserver{


    @Autowired
    private UdpDeviceDataTemplateService udpDeviceDataTemplateService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        UdpCmd08DataVO udpCmd08DataVO = uplinkDataVO.getUdpCmd08DataVO();
        if(Objects.isNull(udpCmd08DataVO))
            return;

        List<UdpDeviceDataTemplateDO> saveList = new ArrayList<>();
        udpCmd08DataVO.getRoomDataVOList().forEach(roomDataVO -> {
            UdpDeviceDataTemplateDO udpDeviceRecentDataDO = new UdpDeviceDataTemplateDO();

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
            udpDeviceRecentDataDO.setRoomTemperature(roomDataVO.getRoomTemperature());
            saveList.add(udpDeviceRecentDataDO);
        });
        udpDeviceDataTemplateService.saveBatch(saveList);
//            udpCmd08DataVO.getRoomDataVOList().sort(Comparator.comparing(RoomDataVO::getCollectTime).reversed());

    }
}
