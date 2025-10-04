package com.ruoyi.business.iot.handler.uplink;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.MqttService;
import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 接收到上传的数据后 应答一个时间戳给设备,用于设备校验时间
 */
@Slf4j
@Component
public class MqttCheckTimeObserver extends AbstractUplinkMsgObserver{

    @Autowired
    MqttService mqttService;

    @Autowired
    DeviceMapper deviceMapper;

    private static final ConcurrentHashMap<String, LocalDateTime> timeMap = new ConcurrentHashMap();

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        if(CollectionUtils.isEmpty(uplinkDataVO.getMqttCmd08DataVOS()))
            return;
        String deviceSn = uplinkDataVO.getMqttCmd08DataVOS().get(0).getDeviceSn();
        LocalDateTime latestPushTime = timeMap.get(deviceSn);
        if(Objects.nonNull(latestPushTime)&& latestPushTime.plusHours(1).compareTo(LocalDateTime.now()) > 0){
            return;
        }
        log.info("开始准备下发时间校验deviceSn={}",deviceSn);
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = deviceMapper.selectOne(queryWrapper);
        if(Objects.isNull(deviceDO)){
            log.error("准备下发校验时间时未找到设备deviceSn={}",deviceSn);
            return;
        }
        DtuDownDataVO dtuDownDataVO = buildCommand(uplinkDataVO);

        try {
            mqttService.publish(deviceDO.getDtuSn(),dtuDownDataVO);
            timeMap.put(deviceSn,LocalDateTime.now());
        } catch (Exception e) {
            log.error("发布mqtt消息出错了 deviceSn={}",deviceSn,e);
        }

    }

    private static DtuDownDataVO buildCommand(UplinkDataVO uplinkDataVO) {
        List<CommonDownDataVO> commonDownDataVOS = uplinkDataVO.getMqttCmd08DataVOS()
                .stream()
                .map(mqttCmd08DataVO -> CommonDownDataVO.builder()
                        .cmdCode(DownCmdEnum.DOWNLINK_FF.getCode())
                        .readWriteFlag(ReadWriteEnum.RESPONSE.getCode())
                        .deviceSn(mqttCmd08DataVO.getDeviceSn())
                        .build())
                .collect(Collectors.toList());
        DtuDownDataVO dtuDownDataVO = DtuDownDataVO.builder()
                .dataVOList(commonDownDataVOS)
                .build();
        return dtuDownDataVO;
    }
}
