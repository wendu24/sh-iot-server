package com.ruoyi.business.iot.common.vo;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
public class DtuDataVO {


    /**
     * 电池电量
     */
    private Integer batteryLevel;
    /**
     * 信号强度
     */
    private Integer signalStrength;

    /**
     * 11个字节的 物联网卡ICCID
     */
    private String iccID;

    /**
     * 是否需要平台应答
     */
    private Integer replyFlag;
    /**
     * 指令条数
     */
    private Integer cmdNum;


    private List<UplinkCmd08DataVO> cmd08DataVOS;

    private List<UplinkCmdFFDataVO> cmdFFDataVOS;


    public void addCmd08DataVOS(UplinkCmd08DataVO uplinkCmd08DataVO){
        if(CollectionUtils.isEmpty(cmd08DataVOS)){
            this.cmd08DataVOS = new ArrayList<>();
        }
        cmd08DataVOS.add(uplinkCmd08DataVO);
    }


    public void addCmdFFDataVOS(UplinkCmdFFDataVO uplinkCmdFFDataVO){
        if(CollectionUtils.isEmpty(cmdFFDataVOS)){
            this.cmdFFDataVOS = new ArrayList<>();
        }
        cmdFFDataVOS.add(uplinkCmdFFDataVO);
    }


}
