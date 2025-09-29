package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceLatestDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.service.DeviceLatestDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用 topic:data中的数据更新设备的最新信息
 */
@Slf4j
@Component
public class LatestDeviceDataObserver extends AbstractUplinkMsgObserver {

    @Autowired
    DeviceLatestDataService deviceLatestDataService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        List<MqttCmd08DataVO> mqttCmd08DataVOS = uplinkDataVO.getMqttCmd08DataVOS();
        if(CollectionUtils.isEmpty(mqttCmd08DataVOS))
            return;
        List<DeviceLatestDataDO> updateList = new ArrayList<>();
        List<DeviceLatestDataDO> addList = new ArrayList<>();
        /**
         * 查找数据库数据
         */
        Map<String, DeviceLatestDataDO> dbDataMap = listFromDb(uplinkDataVO);
        /**
         * 构造数据
         */
        buildSaveUpdateList(uplinkDataVO, dbDataMap, addList, updateList);
        /**
         * 数据入库
         */
        if(CollectionUtils.isNotEmpty(updateList)){
            deviceLatestDataService.updateBatchById(updateList);
        }
        if(CollectionUtils.isNotEmpty(addList)){
            deviceLatestDataService.saveBatch(addList);
        }
    }

    private static void buildSaveUpdateList(UplinkDataVO uplinkDataVO,
                                            Map<String, DeviceLatestDataDO> dbDataMap,
                                            List<DeviceLatestDataDO> addList,
                                            List<DeviceLatestDataDO> updateList
    ) {

        uplinkDataVO.getMqttCmd08DataVOS().forEach(mqttCmd08DataVO -> {

            DeviceLatestDataDO dbData = dbDataMap.get(mqttCmd08DataVO.getDeviceSn());
            DeviceLatestDataDO deviceLatestDataDO = new DeviceLatestDataDO();
            BeanUtil.copyProperties(mqttCmd08DataVO,deviceLatestDataDO);
            String abnormalTypes = mqttCmd08DataVO.getAbnormalTypes()
                    .stream()
                    .map(AbnormalTypeEnum::getCode)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(abnormalTypes))
                deviceLatestDataDO.setAbnormalTypes(abnormalTypes);
            deviceLatestDataDO.setUplinkPeriod(mqttCmd08DataVO.getUplinkPeriod().intValue());
            deviceLatestDataDO.setCreateTime(LocalDateTime.now());
            if(Objects.isNull(dbData)){
                addList.add(deviceLatestDataDO);
            }else{
                deviceLatestDataDO.setId(dbData.getId());
                updateList.add(deviceLatestDataDO);
            }
        });
    }

    private Map<String, DeviceLatestDataDO> listFromDb(UplinkDataVO uplinkDataVO) {
        List<String> deviceSn = uplinkDataVO.getMqttCmd08DataVOS().stream().map(MqttCmd08DataVO::getDeviceSn).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<DeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceLatestDataDO::getDeviceSn,deviceSn);
        queryWrapper.select(DeviceLatestDataDO::getId,DeviceLatestDataDO::getDeviceSn);
        Map<String, DeviceLatestDataDO> dbDataMap = deviceLatestDataService.list(queryWrapper).stream().collect(Collectors.toMap(DeviceLatestDataDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));
        return dbDataMap;
    }

}
