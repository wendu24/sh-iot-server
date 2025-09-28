package com.ruoyi.business.iot.observer;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceLatestDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotMsg;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmd08DataVO;
import com.ruoyi.business.service.DeviceLatestDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
public class LatestDeviceDataObserver implements MqttMsgObserver {

    @Autowired
    DeviceLatestDataService deviceLatestDataService;

    @Override
    public void handle(String topic, IotMsg iotMsg) {
        DtuDataVO dtuDataVO = (DtuDataVO) iotMsg;
        List<DeviceLatestDataDO> updateList = new ArrayList<>();
        List<DeviceLatestDataDO> addList = new ArrayList<>();
        /**
         * 查找数据库数据
         */
        Map<String, DeviceLatestDataDO> dbDataMap = listFromDb(dtuDataVO);
        /**
         * 构造数据
         */
        buildSaveUpdateList(dtuDataVO, dbDataMap, addList, updateList);
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

    private static void buildSaveUpdateList(DtuDataVO dtuDataVO,
                                            Map<String, DeviceLatestDataDO> dbDataMap,
                                            List<DeviceLatestDataDO> addList,
                                            List<DeviceLatestDataDO> updateList
    ) {

        dtuDataVO.getCmd08DataVOS().forEach(uplinkCmd08DataVO -> {

            DeviceLatestDataDO dbData = dbDataMap.get(uplinkCmd08DataVO.getDeviceSn());
            DeviceLatestDataDO deviceLatestDataDO = new DeviceLatestDataDO();
            BeanUtil.copyProperties(uplinkCmd08DataVO,deviceLatestDataDO);
            String abnormalTypes = uplinkCmd08DataVO.getAbnormalTypes()
                    .stream()
                    .map(AbnormalTypeEnum::getCode)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(abnormalTypes))
                deviceLatestDataDO.setAbnormalTypes(abnormalTypes);
            deviceLatestDataDO.setUplinkPeriod(uplinkCmd08DataVO.getUplinkPeriod().intValue());
            deviceLatestDataDO.setCreateTime(LocalDateTime.now());
            if(Objects.isNull(dbData)){
                addList.add(deviceLatestDataDO);
            }else{
                deviceLatestDataDO.setId(dbData.getId());
                updateList.add(deviceLatestDataDO);
            }
        });
    }

    private Map<String, DeviceLatestDataDO> listFromDb(DtuDataVO dtuDataVO) {
        List<String> deviceSn = dtuDataVO.getCmd08DataVOS().stream().map(UplinkCmd08DataVO::getDeviceSn).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<DeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceLatestDataDO::getDeviceSn,deviceSn);
        queryWrapper.select(DeviceLatestDataDO::getId,DeviceLatestDataDO::getDeviceSn);
        Map<String, DeviceLatestDataDO> dbDataMap = deviceLatestDataService.list(queryWrapper).stream().collect(Collectors.toMap(DeviceLatestDataDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));
        return dbDataMap;
    }

    @Override
    @PostConstruct
    public void register() {
        MqttMsgProducer.addHandler(TopicConstant.UNIT_DATA,this);
    }
}
