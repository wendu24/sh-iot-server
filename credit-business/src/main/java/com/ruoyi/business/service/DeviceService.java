package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.vo.DeviceVO;

import java.util.List;
import java.util.Map;

public interface DeviceService extends IService<DeviceDO> {


    public void update( DeviceVO deviceVO);

    public Page<DeviceDO> list(DeviceVO deviceVO);

    public void add(DeviceVO deviceVO);

    public void publishMsg(List<CommonDownDataVO> commonDownDataVOS);

    public DeviceDO findByDeviceSn(String deviceSn) throws Exception ;

    public Map<String,DeviceDO> findByDeviceSn(List<String> deviceSns)  ;

    public List<DeviceDO> findByDeviceSn2(List<String> deviceSns);


}
