package com.ruoyi.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.validate.CreateGroup;
import com.ruoyi.business.vo.DeviceVO;
import com.ruoyi.business.vo.RefreshDeviceVO;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/biz/device")
public class DeviceController {


    @Autowired
    DeviceService deviceService;

    @RequestMapping("/list")
    public Page<DeviceDO> list(@RequestBody DeviceVO deviceVO) {
        return deviceService.list(deviceVO);
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


    @RequestMapping("/public-msg")
    public AjaxResult publicMsg(@RequestBody @Validated List<CommonDownDataVO> commonDownDataVOS){
        deviceService.publishMsg(commonDownDataVOS);
        return AjaxResult.success();
    }


    @RequestMapping("/refresh")
    public AjaxResult refresh(@RequestBody RefreshDeviceVO refreshDeviceVO){
        deviceService.refreshData(refreshDeviceVO);
        return AjaxResult.success();
    }

    @RequestMapping("/delete")
    public AjaxResult delete(@RequestBody DeviceVO deviceVO) {
        deviceService.removeById(deviceVO.getId());
        return AjaxResult.success();
    }


}
