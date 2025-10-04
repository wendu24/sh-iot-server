package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.mapper.DeviceMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceDO> implements DeviceService {


    public DeviceDO findByDeviceSn(String deviceSn) throws Exception {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = this.getOne(queryWrapper);
        if(Objects.isNull(deviceDO))
            throw new Exception("设备未找到" + deviceSn);
        return deviceDO;
    }

}
