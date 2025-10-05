package com.ruoyi.business.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.domain.StatHourDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import com.ruoyi.business.service.StatHourService;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
import com.ruoyi.business.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatHourJob {

    @Autowired
    private MqttDeviceRecentDataService mqttDeviceRecentDataService;

    @Autowired
    private UdpDeviceRecentDataService udpDeviceRecentDataService;

    @Autowired
    private StatHourService statHourService;

    /**
     * 每个小时统计一次,统计前两个小时的数据
     */
    @Scheduled(cron = "0 10 * * * ?")
    public void statHour() {
        log.info("开始统计数据");
        LocalDateTime startTime = LocalDateTime.now().plusHours(-2).withMinute(0).withSecond(0);
        LocalDateTime endTime = LocalDateTime.now().plusHours(-1).withMinute(0).withSecond(0);
        /**
         * 先删除
         */
        removeByStartTime(startTime);

        /**
         * 统计MQTT数据
         */
        Map<String, StatHourDO> statHourDOMap = statMqttDataList(startTime, endTime);
        /**
         * 统计UDP数据
         */
        statUdpDataList(startTime, endTime, statHourDOMap);

        List<StatHourDO> saveList = statHourDOMap.values().stream().filter(statHourDO -> Objects.nonNull(statHourDO.getAvgTemperature())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(saveList))
            statHourService.saveBatch(saveList);

    }

    private void removeByStartTime(LocalDateTime startTime) {
        String statDay = DateUtil.formatLocalDateTime(startTime, DateUtil.YYYY_MM_DD);
        int hour = startTime.getHour();
        LambdaQueryWrapper<StatHourDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StatHourDO::getStatDay,statDay);
        queryWrapper.eq(StatHourDO::getStatHour,hour);
        statHourService.remove(queryWrapper);
    }

    private void statUdpDataList(LocalDateTime startTime, LocalDateTime endTime, Map<String, StatHourDO> statHourDOMap) {
        LambdaQueryWrapper<UdpDeviceRecentDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(UdpDeviceRecentDataDO::getCollectTime, startTime);
        queryWrapper.lt(UdpDeviceRecentDataDO::getCollectTime, endTime);
        queryWrapper.isNotNull(UdpDeviceRecentDataDO::getCommunityId);
        List<UdpDeviceRecentDataDO> udpDataList = udpDeviceRecentDataService.list(queryWrapper);
        udpDataList.stream().collect(Collectors.groupingBy(UdpDeviceRecentDataDO::groupKey))
                .forEach((groupKey,dataList)->{
                    try {
                        UdpDeviceRecentDataDO udpDeviceRecentDataDO = dataList.get(0);
                        String statDay = DateUtil.formatLocalDateTime(udpDeviceRecentDataDO.getCollectTime(), DateUtil.YYYY_MM_DD);
                        int hour = udpDeviceRecentDataDO.getCollectTime().getHour();
                        String mapKey = mapKey(udpDeviceRecentDataDO.getCommunityId(), statDay, hour);
                        StatHourDO statHourDO = statHourDOMap.get(mapKey);
                        if(Objects.isNull(statHourDO))
                            return;
                        statHourDO.setAvgTemperature(calAvgRoomTemperature(dataList));
                        statHourDO.setAvgHumidity(calAvgRoomHumidity(dataList));
                    } catch (Exception e) {
                        log.error("统计数据出错啦groupKey={}",groupKey,e);
                    }
                });
    }


    private Map<String,StatHourDO> statMqttDataList(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<MqttDeviceRecentDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(MqttDeviceRecentDataDO::getCollectionTime, startTime);
        queryWrapper.lt(MqttDeviceRecentDataDO::getCollectionTime, endTime);
        queryWrapper.isNotNull(MqttDeviceRecentDataDO::getCommunityId);
        List<MqttDeviceRecentDataDO> mqttDates = mqttDeviceRecentDataService.list(queryWrapper);
        List<StatHourDO> saveDateList = new ArrayList<>();
        mqttDates.stream().collect(Collectors.groupingBy(MqttDeviceRecentDataDO::groupKey))
                .forEach((groupKey, dataList) -> {

                    try {
                        MqttDeviceRecentDataDO recentDataDO = dataList.get(0);
                        StatHourDO statHourDO = new StatHourDO();
                        statHourDO.setCommunityId(recentDataDO.getCommunityId());
                        statHourDO.setCommunityName(recentDataDO.getCommunityName());
                        statHourDO.setStatDay(DateUtil.formatLocalDateTime(recentDataDO.getCollectionTime(), DateUtil.YYYY_MM_DD));
                        statHourDO.setStatHour(recentDataDO.getCollectionTime().getHour());
//                    statHourDO.setAvgTemperature();
//                    statHourDO.setAvgHumidity();
                        statHourDO.setAvgReturnWaterPressure(calAvgReturnWaterPressure(dataList));
                        statHourDO.setAvgSupplyWaterPressure(calAvgSupplyWaterPressure(dataList));
                        statHourDO.setAvgReturnWaterTemperature(calAvgReturnWaterTemperature(dataList));
                        statHourDO.setAvgSupplyWaterTemperature(calAvgSupplyWaterTemperature(dataList));
                        statHourDO.setAvgValvePosition(calAvgValvePosition(dataList));
                        statHourDO.setCreateTime(LocalDateTime.now());
                        saveDateList.add(statHourDO);
                    } catch (Exception e) {
                        log.error("统计数据出错啦groupKey={}",groupKey,e);
                    }
                });
        return saveDateList.stream().collect(Collectors.toMap(StatHourDO::mapKey, Function.identity(),(t1,t2)->t1));
    }



    private static BigDecimal calAvgRoomTemperature(List<UdpDeviceRecentDataDO> dataList) {
        Double value = dataList.stream().map(UdpDeviceRecentDataDO::getRoomTemperature)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2,RoundingMode.HALF_UP);
    }


    private static BigDecimal calAvgRoomHumidity(List<UdpDeviceRecentDataDO> dataList) {
        Double value = dataList.stream().map(UdpDeviceRecentDataDO::getRoomHumidity)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2,RoundingMode.HALF_UP);
    }


    private BigDecimal calAvgReturnWaterPressure(List<MqttDeviceRecentDataDO> dataList) {
        Double value = dataList.stream()
                .map(MqttDeviceRecentDataDO::getReturnWaterPressure)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }


    private BigDecimal calAvgSupplyWaterPressure(List<MqttDeviceRecentDataDO> dataList) {
        Double value = dataList.stream()
                .map(MqttDeviceRecentDataDO::getSupplyWaterPressure)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calAvgReturnWaterTemperature(List<MqttDeviceRecentDataDO> dataList) {
        Double value = dataList.stream()
                .map(MqttDeviceRecentDataDO::getReturnWaterTemperature)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calAvgSupplyWaterTemperature(List<MqttDeviceRecentDataDO> dataList) {
        Double value = dataList.stream()
                .map(MqttDeviceRecentDataDO::getSupplyWaterTemperature)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calAvgValvePosition(List<MqttDeviceRecentDataDO> dataList) {
        Double value = dataList.stream()
                .map(MqttDeviceRecentDataDO::getValvePosition)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
    private String mapKey(Long communityId,String statDay, Integer statHour){
        return communityId + "_" + statDay + "_" + statHour;
    }

}
