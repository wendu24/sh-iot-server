package com.ruoyi.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.service.BizUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BizUserServiceImpl extends ServiceImpl<BizUserMapper, BizUserDO> implements BizUserService {
}
