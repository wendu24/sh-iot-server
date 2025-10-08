package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.vo.RecentDataQueryVO;

public interface UdpDeviceRecentDataService extends IService<UdpDeviceRecentDataDO> {

    public Page<UdpDeviceRecentDataDO> list(RecentDataQueryVO recentDataQueryVO);
}
