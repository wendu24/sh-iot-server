package com.ruoyi.business.iot.common.vo;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传数据解析后统一的对外VO, 包括mqtt和udp
 */
@Data
@SuperBuilder
public class UplinkDataVO implements IotUplinkMsg {

    private DtuDataVO dtuDataVO;


    private List<MqttCmd08DataVO> mqttCmd08DataVOS;

    /**
     *  MQTT UDP 公用字段
     */
    private List<CmdFFDataVO> cmdFFDataVOS;


    /**
     * udp专用
     */
    private UdpCmd08DataVO udpCmd08DataVO;


    public void addCmd08DataVOS(MqttCmd08DataVO mqttCmd08DataVO){
        if(CollectionUtils.isEmpty(mqttCmd08DataVOS)){
            this.mqttCmd08DataVOS = new ArrayList<>();
        }
        mqttCmd08DataVOS.add(mqttCmd08DataVO);
    }


    public void addCmdFFDataVOS(CmdFFDataVO cmdFFDataVO){
        if(CollectionUtils.isEmpty(cmdFFDataVOS)){
            this.cmdFFDataVOS = new ArrayList<>();
        }
        cmdFFDataVOS.add(cmdFFDataVO);
    }


}
