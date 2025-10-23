package com.ruoyi.business.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.MqttService;
import com.ruoyi.business.iot.UdpService;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.validate.CreateGroup;
import com.ruoyi.business.vo.DeviceVO;
import com.ruoyi.business.vo.RefreshDeviceVO;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/biz/device")
public class DeviceController {


    @Autowired
    DeviceService deviceService;

    @Autowired
    MqttService mqttService;

    @Autowired
    UdpService udpService;

    @RequestMapping("/list")
    public Page<DeviceDO> list(@RequestBody DeviceVO deviceVO) {
        return deviceService.list(deviceVO);
    }



    @RequestMapping("/mqtt/public-msg")
    public AjaxResult publicMqttMsg(@RequestBody @Validated List<CommonDownDataVO> commonDownDataVOS){
        log.info("收到下发MQTT消息 commonDownDataVOS={}",JSONObject.toJSONString(commonDownDataVOS));
        deviceService.publishMqttMsg(commonDownDataVOS);
        return AjaxResult.success("消息已下发");
    }


    @RequestMapping("/udp/public-msg")
    public AjaxResult publicUdpMsg(@RequestBody @Validated List<CommonDownDataVO> commonDownDataVOS){
        log.info("收到下发UDP消息 commonDownDataVOS={}",JSONObject.toJSONString(commonDownDataVOS));
        deviceService.publishUdpMsg(commonDownDataVOS);
        return AjaxResult.success("消息发布中,十分钟之后查看下发结果");
    }




    /**
     * 文件导入接口
     * 对应前端请求: POST /sh-iot/import
     */
    @PostMapping("/import")
    public AjaxResult importFile(@RequestParam("file") MultipartFile file) {

        try{
        if (file.isEmpty()) {
            return AjaxResult.error("文件为空");
        }

            // 2. 检查是否为 Excel 文件
        String filename = file.getOriginalFilename();
        if (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls")) {
            return AjaxResult.error("只支持表格导入");
        }
        log.info("接收到导入请求 filename={}",filename);


            return AjaxResult.success("导入成功");
        } catch (Exception e) {
           log.error("导入设备出错啦",e);
           return AjaxResult.error("导入失败");
        }
    }


    @RequestMapping("/add")
    public AjaxResult add(@RequestBody @Validated(CreateGroup.class) DeviceVO deviceVO) {
        try {
            deviceService.add(deviceVO);
        } catch (Exception e) {
            log.error("新增出错啦={}", deviceVO.getDeviceSn(), e);
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success();
    }

    @RequestMapping("/update")
    public AjaxResult update(@RequestBody DeviceVO deviceVO){
        deviceService.update(deviceVO);
        return AjaxResult.success();
    }

    /**
     * 单条不用缓存
     * @param refreshDeviceVO
     * @return
     */
    @RequestMapping("/refresh")
    public AjaxResult refresh(@RequestBody RefreshDeviceVO refreshDeviceVO){
        log.info("请求刷新数据refreshDeviceVO={}",JSONObject.toJSONString(refreshDeviceVO));
        deviceService.refreshData(refreshDeviceVO);
        return AjaxResult.success();
    }


    @RequestMapping("/delete")
    public AjaxResult delete(@RequestBody DeviceVO deviceVO) {
        deviceService.removeById(deviceVO.getId());
        return AjaxResult.success();
    }


}
