package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.*;
import com.ruoyi.business.service.*;
import com.ruoyi.business.util.DateUtil;
import com.ruoyi.business.vo.home.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    StatHourService statHourService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    CommunityService communityService;

    @Autowired
    MqttDeviceLatestDataService mqttDeviceLatestDataService;

    @Autowired
    UdpDeviceLatestDataService udpDeviceLatestDataService;

    /**
     *  总览: 小区数, 设备数(按类型), 故障设备数(按类型). 最近小时平均室温, 平均设备开度
     */
    @Override
    public OverviewVO overview(HomeQueryVO homeQueryVO){

        int communityNum = queryCommunityNum(homeQueryVO);

        Map<Integer, Long> deviceTypeMap = statDeviceType(homeQueryVO);

        Map<String, Long> abnormalTypeMap = statAbnormalTypes(homeQueryVO);

        OverviewVO overviewVO = OverviewVO.builder()
                .abnormalTypeNum(abnormalTypeMap)
                .communityNum(communityNum)
                .deviceTypeNum(deviceTypeMap)
                .build();

        statRoomData(homeQueryVO, overviewVO);

        return overviewVO;
    }


    @Override
    public List<RoomDataThirtyDayVO> roomDataThirtyDays(HomeQueryVO homeQueryVO){
        List<RoomDataThirtyDayVO> result = new ArrayList<>();
        LambdaQueryWrapper<StatHourDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),StatHourDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.ge(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now().minusDays(30),DateUtil.YYYY_MM_DD));
        queryWrapper.le(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now(),DateUtil.YYYY_MM_DD));
        queryWrapper.select(StatHourDO::getAvgTemperature,StatHourDO::getAvgHumidity, StatHourDO::getStatHour);
        statHourService.list(queryWrapper).stream()
                .collect(Collectors.groupingBy(StatHourDO::getStatHour))
                .forEach((statHour,hourStatList)->{
                    double avgTemp = hourStatList.stream()
                            .map(StatHourDO::getAvgTemperature)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0); // 如果列表为空，返回 0.0

                    double avgHumi = hourStatList.stream()
                            .map(StatHourDO::getAvgHumidity)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0);
                    RoomDataThirtyDayVO dataThirtyDayVO = RoomDataThirtyDayVO.builder()
                            .avgRoomHumidity(BigDecimal.valueOf(avgHumi))
                            .avgRoomTemperature(BigDecimal.valueOf(avgTemp).setScale(2, RoundingMode.HALF_UP))
                            .hour(statHour)
                            .build();
                    result.add(dataThirtyDayVO);
                });
        result.sort(Comparator.comparing(RoomDataThirtyDayVO::getHour));
        return result;
    }


    @Override
    public Top5TemperatureCommunityVO top5TemperatureCommunity(HomeQueryVO homeQueryVO){
        List<RoomDataThirtyDayVO> result = new ArrayList<>();
        LambdaQueryWrapper<StatHourDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),StatHourDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.ge(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now().minusDays(30),DateUtil.YYYY_MM_DD));
        queryWrapper.le(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now(),DateUtil.YYYY_MM_DD));
        queryWrapper.select(StatHourDO::getAvgTemperature,StatHourDO::getAvgHumidity,StatHourDO::getAvgSupplyWaterPressure,StatHourDO::getAvgSupplyWaterTemperature, StatHourDO::getCommunityId, StatHourDO::getCommunityName);
        statHourService.list(queryWrapper)
                .stream()
                .collect(Collectors.groupingBy(StatHourDO::getCommunityName))
                .forEach((communityName,communityStatList)->{
                    double avgTemp = communityStatList.stream()
                            .map(StatHourDO::getAvgTemperature)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0);

                    double avgHumi = communityStatList.stream()
                            .map(StatHourDO::getAvgHumidity)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0);

                    double avgSupplyWaterPressure = communityStatList.stream()
                            .map(StatHourDO::getAvgSupplyWaterPressure)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0);

                    double avgSupplyWaterTemperature = communityStatList.stream()
                            .map(StatHourDO::getAvgSupplyWaterTemperature)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0);
                    RoomDataThirtyDayVO dataThirtyDayVO = RoomDataThirtyDayVO.builder()
                            .avgRoomHumidity(BigDecimal.valueOf(avgHumi))
                            .avgRoomTemperature(BigDecimal.valueOf(avgTemp))
                            .supplyWaterPressure(BigDecimal.valueOf(avgSupplyWaterPressure))
                            .supplyWaterTemperature(BigDecimal.valueOf(avgSupplyWaterTemperature))
                            .communityName(communityName)
                            .build();
                    result.add(dataThirtyDayVO);

                });
        List<RoomDataThirtyDayVO> top5 = result.stream().sorted(Comparator.comparing(RoomDataThirtyDayVO::getAvgRoomTemperature).reversed()).limit(5).collect(Collectors.toList());
        List<RoomDataThirtyDayVO> low5 = result.stream().sorted(Comparator.comparing(RoomDataThirtyDayVO::getAvgRoomTemperature)).limit(5).collect(Collectors.toList());
        return Top5TemperatureCommunityVO.builder()
                .top5(top5)
                .low5(low5)
                .build();


    }


    /**
     * 阀门开度和温度散点图
     * @param homeQueryVO
     */
    @Override
    public List<StatHourDO> scatterChart(HomeQueryVO homeQueryVO){
        LambdaQueryWrapper<StatHourDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),StatHourDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.ge(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now().minusDays(30),DateUtil.YYYY_MM_DD));
        queryWrapper.le(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now(),DateUtil.YYYY_MM_DD));
        queryWrapper.select(StatHourDO::getAvgValvePosition,StatHourDO::getAvgTemperature, StatHourDO::getAvgSupplyWaterPressure,StatHourDO::getAvgSupplyWaterTemperature);
        List<StatHourDO> originalList = statHourService.list(queryWrapper);
        /**
         * 简易抽稀
         */
        List<StatHourDO> result = new ArrayList<>();
        // 从第一个点开始, 每隔 samplingFactor 个点取一个
        for (int i = 0; i < originalList.size(); i += 10) {
            result.add(originalList.get(i));
        }

        return result;

    }


    @Override
    public  List<WaterTemperatureVO> waterTemperatureAndHour(HomeQueryVO homeQueryVO){
        List<WaterTemperatureVO> result = new ArrayList<>();
        LambdaQueryWrapper<StatHourDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),StatHourDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.ge(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now().minusDays(30),DateUtil.YYYY_MM_DD));
        queryWrapper.le(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now(),DateUtil.YYYY_MM_DD));
        queryWrapper.select(StatHourDO::getAvgSupplyWaterTemperature,StatHourDO::getAvgReturnWaterTemperature, StatHourDO::getStatHour);
        statHourService.list(queryWrapper).stream()
                .collect(Collectors.groupingBy(StatHourDO::getStatHour))
                .forEach((statHour,hourStatList)->{
                    double avgReturnWaterTemperature = hourStatList.stream()
                            .map(StatHourDO::getAvgReturnWaterTemperature)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0); // 如果列表为空，返回 0.0

                    double avgSupplyWaterTemperature = hourStatList.stream()
                            .map(StatHourDO::getAvgSupplyWaterTemperature)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(0.0);
                    WaterTemperatureVO temperatureVO = WaterTemperatureVO.builder()
                            .hour(statHour)
                            .avgReturnWaterTemperature(BigDecimal.valueOf(avgReturnWaterTemperature))
                            .avgSupplyWaterTemperature(BigDecimal.valueOf(avgSupplyWaterTemperature))
                            .build();
                    result.add(temperatureVO);

                });
        result.sort(Comparator.comparing(WaterTemperatureVO::getHour));
        return result;
    }




    /**
     * 统计当天的最近一个小时的数据
     * @param homeQueryVO
     * @param overviewVO
     */
    private void statRoomData(HomeQueryVO homeQueryVO,OverviewVO overviewVO) {
        LambdaQueryWrapper<StatHourDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),StatHourDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.eq(StatHourDO::getStatDay, DateUtil.formatLocalDateTime(LocalDateTime.now(),DateUtil.YYYY_MM_DD));
        queryWrapper.eq(StatHourDO::getStatHour,LocalDateTime.now().plusHours(-3).getHour());
        queryWrapper.select(StatHourDO::getAvgTemperature,StatHourDO::getAvgHumidity, StatHourDO::getAvgValvePosition);
        List<StatHourDO> records = statHourService.list(queryWrapper);
        // 计算两个字段的平均值
        double avgTemp = records.stream()
                .map(StatHourDO::getAvgTemperature)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0); // 如果列表为空，返回 0.0

        double avgHumi = records.stream()
                .map(StatHourDO::getAvgHumidity)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0); // 如果列表为空，返回 0.0

        double avgValve = records.stream()
                .map(StatHourDO::getAvgValvePosition)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0); // 如果列表为空，返回 0.0
        overviewVO.setAvgRoomTemperature(BigDecimal.valueOf(avgTemp));
        overviewVO.setAvgRoomHumidity(BigDecimal.valueOf(avgHumi));
        overviewVO.setAvgValvePosition(BigDecimal.valueOf(avgValve));
    }

    private Map<String,Long> statAbnormalTypes(HomeQueryVO homeQueryVO) {
        LambdaQueryWrapper<MqttDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),MqttDeviceLatestDataDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.select(MqttDeviceLatestDataDO::getAbnormalTypes);
        Map<String, Long> mqttAbnormalTypeCount = mqttDeviceLatestDataService
                .list(queryWrapper)
                .stream()
                .filter(Objects::nonNull)
                .map(MqttDeviceLatestDataDO::getAbnormalTypes)         // 提取 abnormalTypes 字符串
                .filter(Objects::nonNull)                              // 过滤 null 值
                .filter(s -> !s.trim().isEmpty())                      // 过滤空字符串
                .flatMap(s -> Arrays.stream(s.split(",")))             // 按逗号切分，并打平成单个元素流
                .map(String::trim)                                     // 去除空格（可选）
                .filter(str -> !str.isEmpty())                         // 再次过滤空字符串
                .collect(Collectors.groupingBy(
                        Function.identity(),                               // 当前元素作为 key（即异常类型）
                        Collectors.counting()                             // 统计频次
                ));

        LambdaQueryWrapper<UdpDeviceLatestDataDO> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),UdpDeviceLatestDataDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper2.select(UdpDeviceLatestDataDO::getAbnormalTypes);
        Map<String, Long> udpAbnormalTypeCount = udpDeviceLatestDataService
                .list(queryWrapper2)
                .stream()
                .filter(Objects::nonNull)
                .map(UdpDeviceLatestDataDO::getAbnormalTypes)         // 提取 abnormalTypes 字符串
                .filter(Objects::nonNull)                              // 过滤 null 值
                .filter(s -> !s.trim().isEmpty())                      // 过滤空字符串
                .flatMap(s -> Arrays.stream(s.split(",")))             // 按逗号切分，并打平成单个元素流
                .map(String::trim)                                     // 去除空格（可选）
                .filter(str -> !str.isEmpty())                         // 再次过滤空字符串
                .collect(Collectors.groupingBy(
                        Function.identity(),                               // 当前元素作为 key（即异常类型）
                        Collectors.counting()                             // 统计频次
                ));

        Map<String, Long> merged = new HashMap<>(mqttAbnormalTypeCount);

        udpAbnormalTypeCount.forEach((key, value) ->
                merged.merge(key, value, Long::sum)
        );
        return merged;
    }

    private Map<Integer, Long> statDeviceType(HomeQueryVO homeQueryVO) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),DeviceDO::getCommunityId, homeQueryVO.getCommunityIds());
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        queryWrapper.select(DeviceDO::getDeviceType);
        return deviceService.list(queryWrapper).stream()
                .collect(Collectors.groupingBy(
                        DeviceDO::getDeviceType,
                        Collectors.counting()
                ));
    }

    private int queryCommunityNum(HomeQueryVO homeQueryVO) {
        LambdaQueryWrapper<CommunityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(homeQueryVO.getCommunityIds()),CommunityDO::getId, homeQueryVO.getCommunityIds());
        queryWrapper.eq(CommunityDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        return communityService.count(queryWrapper);
    }


}
