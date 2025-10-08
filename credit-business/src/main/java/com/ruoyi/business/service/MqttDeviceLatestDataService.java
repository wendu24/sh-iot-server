package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.vo.DtuLatestDataVO;
import com.ruoyi.business.vo.MqttLatestDataVO;

public interface MqttDeviceLatestDataService extends IService<MqttDeviceLatestDataDO> {


    public MqttLatestDataVO findMqttByDeviceSN(String deviceSn);
    public DtuLatestDataVO findDtuByDeviceSN(String deviceSn);
}
