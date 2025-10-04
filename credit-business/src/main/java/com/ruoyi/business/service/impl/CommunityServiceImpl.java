package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.CommunityDO;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.mapper.CommunityMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.service.CommunityService;
import com.ruoyi.business.vo.CommunityVO;
import com.ruoyi.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityDO> implements CommunityService {


    @Override
    public void add(CommunityVO communityVO){
        LambdaQueryWrapper<CommunityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityDO::getName,communityVO.getName());
        queryWrapper.eq(CommunityDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        CommunityDO dbData = getOne(queryWrapper);
        if(Objects.nonNull(dbData))
            throw new ServiceException("重复的小区名称");
        CommunityDO newData = new CommunityDO();
        BeanUtil.copyProperties(communityVO,newData);
        newData.setCreateTime(LocalDateTime.now());
        newData.setUpdateTime(LocalDateTime.now());
        save(newData);
    }


}
