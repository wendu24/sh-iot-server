package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MqttDeviceDataTemplateDO;
import com.ruoyi.business.domain.UdpDeviceDataTemplateDO;
import com.ruoyi.business.mapper.MqttDeviceDataTemplateMapper;
import com.ruoyi.business.mapper.UdpDeviceDataTemplateMapper;
import com.ruoyi.business.service.MqttDeviceDataTemplateService;
import com.ruoyi.business.service.UdpDeviceDataTemplateService;
import org.springframework.stereotype.Service;

@Service
public class MqttDeviceDataTemplateServiceImpl extends ServiceImpl<MqttDeviceDataTemplateMapper, MqttDeviceDataTemplateDO> implements MqttDeviceDataTemplateService {
}
