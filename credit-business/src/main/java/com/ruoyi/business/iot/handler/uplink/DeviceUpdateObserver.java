package com.ruoyi.business.iot.handler.uplink;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MsgSetReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 将回复消息数据更新到设备表
 */
@Slf4j
@Component
public class DeviceUpdateObserver extends AbstractUplinkMsgObserver {

    @Autowired
    private MsgSetReplyService msgSetReplyService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        List<CmdFFDataVO> cmdFFDataVOS = uplinkDataVO.getCmdFFDataVOS();
        if(CollectionUtils.isEmpty(cmdFFDataVOS))
            return;
        // 这里一般只有一条数据,所以循环查数据库了
        cmdFFDataVOS.forEach(cmdFFDataVO -> {
            LambdaQueryWrapper<MsgSetReplyDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MsgSetReplyDO::getDeviceSn,cmdFFDataVO.getDeviceSn());
            queryWrapper.eq(MsgSetReplyDO::getMid,cmdFFDataVO.getMid());
            queryWrapper.ge(MsgSetReplyDO::getCreateTime, LocalDateTime.now().plusHours(-5));
            MsgSetReplyDO publishData = msgSetReplyService.getOne(queryWrapper);
            Byte readWriteFlag = cmdFFDataVO.getReadWriteFlag();
            log.info("收到设备回复信息,准备更新设备数据={}",JSONObject.toJSONString(cmdFFDataVO));
            String data =null;
            if(readWriteFlag.equals(ReadWriteEnum.READ.getCode().byteValue())){
                data = cmdFFDataVO.getData();
            }else {
                if(cmdFFDataVO.getResult() != 0)
                    return;
                CommonDownDataVO commonDownDataVO = JSONObject.parseObject(publishData.getMsgBody(), CommonDownDataVO.class);
                data = StringUtils.isNotBlank(commonDownDataVO.getDataStr())?commonDownDataVO.getDataStr():commonDownDataVO.getData().toString();
            }
            if(StringUtils.isBlank(data))
                return;

            updateDevice(cmdFFDataVO, data);
        });
    }

    private void updateDevice(CmdFFDataVO cmdFFDataVO ,String data) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn, cmdFFDataVO.getDeviceSn());
        DeviceDO deviceDO = deviceService.getOne(queryWrapper);
        if (Objects.isNull(deviceDO)) {
            log.error("未找到设备 {}", cmdFFDataVO.getDeviceSn());
            return;
        }
        DeviceDO update = new DeviceDO();
        update.setId(deviceDO.getId());
        byte cmdCode = cmdFFDataVO.getCmdCode();
        switch (cmdCode) {
            case 22:
                update.setIpAddress(data);
                break;
            case 25:
            case 27:
                update.setAesKey(data);
                break;
            case 38:
                update.setLoginKey(data);
                break;
            case 35:
                update.setReportPeriod(Integer.parseInt(data));
                break;
            case 37:
                update.setCollectPeriod(Integer.parseInt(data));
                break;
            case 48:
                update.setValvePosition(new BigDecimal(data));
                break;
            case 49:
                update.setReturnWaterTemperature(new BigDecimal(data));
                break;
            case 50:
                update.setRoomTemperature(new BigDecimal(data));
                break;
            case 51:
            case 52:
            case 63:
                break;
            default:
                log.error("未知的cmd={}", cmdCode);
                break;

        }
        deviceService.updateById(update);
        log.info("设备数据更新成功update={}",JSONObject.toJSONString(update));
    }



}