package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.mapper.MqttDeviceLatestDataMapper;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MqttDeviceLatestDataServiceImpl extends ServiceImpl<MqttDeviceLatestDataMapper, MqttDeviceLatestDataDO> implements MqttDeviceLatestDataService {
}
