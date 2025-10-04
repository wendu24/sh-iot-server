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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceDO> implements DeviceService {


    @Override
    public DeviceDO findByDeviceSn(String deviceSn) throws Exception {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = this.getOne(queryWrapper);
        if(Objects.isNull(deviceDO))
            throw new Exception("设备未找到" + deviceSn);
        return deviceDO;
    }

    @Override
    public Map<String,DeviceDO> findByDeviceSn(List<String> deviceSns)  {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceDO::getDeviceSn,deviceSns);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        return list(queryWrapper).stream().collect(Collectors.toMap(DeviceDO::getDeviceSn, Function.identity(),(t1,t2)->t1));
    }

}
