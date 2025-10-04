package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.DeviceDO;

import java.util.List;
import java.util.Map;

public interface DeviceService extends IService<DeviceDO> {

    public DeviceDO findByDeviceSn(String deviceSn) throws Exception ;

    public Map<String,DeviceDO> findByDeviceSn(List<String> deviceSns)  ;


}
