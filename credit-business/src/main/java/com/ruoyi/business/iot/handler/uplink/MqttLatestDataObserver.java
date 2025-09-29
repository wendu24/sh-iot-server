package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
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
public class MqttLatestDataObserver extends AbstractUplinkMsgObserver {

    @Autowired
    MqttDeviceLatestDataService mqttDeviceLatestDataService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        List<MqttCmd08DataVO> mqttCmd08DataVOS = uplinkDataVO.getMqttCmd08DataVOS();
        if(CollectionUtils.isEmpty(mqttCmd08DataVOS))
            return;
        List<MqttDeviceLatestDataDO> updateList = new ArrayList<>();
        List<MqttDeviceLatestDataDO> addList = new ArrayList<>();
        /**
         * 查找数据库数据
         */
        Map<String, MqttDeviceLatestDataDO> dbDataMap = listFromDb(uplinkDataVO);
        /**
         * 构造数据
         */
        buildSaveUpdateList(uplinkDataVO, dbDataMap, addList, updateList);
        /**
         * 数据入库
         */
        if(CollectionUtils.isNotEmpty(updateList)){
            mqttDeviceLatestDataService.updateBatchById(updateList);
        }
        if(CollectionUtils.isNotEmpty(addList)){
            mqttDeviceLatestDataService.saveBatch(addList);
        }
    }

    private static void buildSaveUpdateList(UplinkDataVO uplinkDataVO,
                                            Map<String, MqttDeviceLatestDataDO> dbDataMap,
                                            List<MqttDeviceLatestDataDO> addList,
                                            List<MqttDeviceLatestDataDO> updateList
    ) {

        uplinkDataVO.getMqttCmd08DataVOS().forEach(mqttCmd08DataVO -> {

            MqttDeviceLatestDataDO dbData = dbDataMap.get(mqttCmd08DataVO.getDeviceSn());
            MqttDeviceLatestDataDO mqttDeviceLatestDataDO = new MqttDeviceLatestDataDO();
            BeanUtil.copyProperties(mqttCmd08DataVO, mqttDeviceLatestDataDO);
            String abnormalTypes = mqttCmd08DataVO.getAbnormalTypes()
                    .stream()
                    .map(AbnormalTypeEnum::getCode)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(abnormalTypes))
                mqttDeviceLatestDataDO.setAbnormalTypes(abnormalTypes);
            mqttDeviceLatestDataDO.setUplinkPeriod(mqttCmd08DataVO.getUplinkPeriod().intValue());
            mqttDeviceLatestDataDO.setCreateTime(LocalDateTime.now());
            if(Objects.isNull(dbData)){
                addList.add(mqttDeviceLatestDataDO);
            }else{
                mqttDeviceLatestDataDO.setId(dbData.getId());
                updateList.add(mqttDeviceLatestDataDO);
            }
        });
    }

    private Map<String, MqttDeviceLatestDataDO> listFromDb(UplinkDataVO uplinkDataVO) {
        List<String> deviceSn = uplinkDataVO.getMqttCmd08DataVOS().stream().map(MqttCmd08DataVO::getDeviceSn).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<MqttDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MqttDeviceLatestDataDO::getDeviceSn,deviceSn);
        queryWrapper.select(MqttDeviceLatestDataDO::getId, MqttDeviceLatestDataDO::getDeviceSn);
        Map<String, MqttDeviceLatestDataDO> dbDataMap = mqttDeviceLatestDataService.list(queryWrapper).stream().collect(Collectors.toMap(MqttDeviceLatestDataDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));
        return dbDataMap;
    }

}
