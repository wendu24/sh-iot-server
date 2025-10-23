package com.ruoyi.business.iot;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.MidGenerator;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.handler.DownMsgHandler;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.packager.udp.UdpDataPackager;
import com.ruoyi.business.iot.parser.UdpDataParseContext;
import com.ruoyi.business.iot.udp.NettyUdpServer;
import com.ruoyi.business.mapper.DeviceMapper;
import com.ruoyi.business.util.RedisKeyUtil;
import com.ruoyi.business.vo.UdpManualDownVO;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UdpService {

    @Autowired
    private DownMsgHandler downMsgHandler;

    @Autowired
    private MidGenerator midGenerator;

    @Autowired
    private UplinkMsgHandler uplinkMsgHandler;

    @Autowired
    private NettyUdpServer nettyUdpServer;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    RedisCache redisCache;

    /**
     * 处理所有接收到的所有的udp数据
     *
     * @param msg 十六进制数据字符串
     */
    public void handleAllMsg(String msg) {
        String sn = UdpDataParseContext.parseSn(msg);
        String aesKey = getAesKey(sn);
        /**
         * 数据解析
         */
        UplinkDataVO uplinkDataVO = UdpDataParseContext.parseData(sn, msg,aesKey);
        log.info("解析出来的数据 headerDataVO={}", uplinkDataVO);
        /**
         * 数据处理
         */
        uplinkMsgHandler.handle(uplinkDataVO);
    }


    /**
     * 直接下发
     *
     * @param sn
     * @param dtuDownDataVO
     * @throws Exception
     */
    @Async
    public void sendCommandAsync(String sn, DtuDownDataVO dtuDownDataVO) {
        try {
            /**
             * 构造数据下发的字节数组
             */
            byte[] dataBytes = UdpDataPackager.build(dtuDownDataVO, sn, getAesKey(sn));
            /**
             * 下发数据
             */
            nettyUdpServer.sendUdpMsg(sn, dataBytes);
            /**
             * 数据下发后,做对应的处理(比如保存记录)
             */
            downMsgHandler.handle(dtuDownDataVO);
        } catch (Exception e) {
            log.error("构建下发数据出错啦dtuDownDataVO={}", JSONObject.toJSONString(dtuDownDataVO), e);
        }
    }


    private String getAesKey(String deviceSn) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = deviceMapper.selectOne(queryWrapper);
        if(Objects.isNull(deviceDO))
            throw new RuntimeException("'未找到设备'" + deviceSn);
        String aesKey = deviceDO.getAesKey();
        return StringUtils.isBlank(aesKey)?AesUtil.DEFAULT_AES_KEY:aesKey;
    }


}
