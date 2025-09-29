package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.domain.UdpDeviceLatestDataDO;
import com.ruoyi.business.mapper.MsgSetReplyMapper;
import com.ruoyi.business.mapper.UdpDeviceLatestDataMapper;
import com.ruoyi.business.service.UdpDeviceLatestDataService;
import org.springframework.stereotype.Service;

@Service
public class UdpDeviceLatestDataServiceImpl extends ServiceImpl<UdpDeviceLatestDataMapper, UdpDeviceLatestDataDO> implements UdpDeviceLatestDataService {
}
