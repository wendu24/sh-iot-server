package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.vo.RecentDataQueryVO;

public interface MqttDeviceRecentDataService extends IService<MqttDeviceRecentDataDO> {

    public Page<MqttDeviceRecentDataDO> list(RecentDataQueryVO recentDataQueryVO);
}
