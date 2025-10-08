package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.UdpDeviceLatestDataDO;
import com.ruoyi.business.vo.UdpLatestDataVO;

public interface UdpDeviceLatestDataService extends IService<UdpDeviceLatestDataDO> {

    public UdpLatestDataVO findByDeviceSN(String deviceSn);
}
