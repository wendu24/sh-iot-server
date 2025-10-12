package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityDO> implements CommunityService {

    @Override
    public Page<CommunityDO> page(CommunityVO communityVO){
        LambdaQueryWrapper<CommunityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(communityVO.getName()),CommunityDO::getName,communityVO.getName());
        queryWrapper.orderByDesc(CommunityDO::getId);
        Page<CommunityDO> pageParam = new Page<>(communityVO.getPageNum(), communityVO.getPageSize());
        Page<CommunityDO> dataPage = this.page(pageParam, queryWrapper);
        return dataPage;
    }
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
        newData.setCode("TJS-" + System.currentTimeMillis());
        newData.setCreateTime(LocalDateTime.now());
        newData.setUpdateTime(LocalDateTime.now());
        save(newData);
    }

    @Override
    public void edit(CommunityVO communityVO){
        CommunityDO dbData = this.getById(communityVO.getId());
        if(Objects.isNull(dbData))
            throw new ServiceException("未找到小区");
        if(!dbData.getName().equals(communityVO.getName())){
            LambdaQueryWrapper<CommunityDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CommunityDO::getName,communityVO.getName());
            queryWrapper.eq(CommunityDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
            CommunityDO sameName = getOne(queryWrapper);
            if(Objects.nonNull(sameName))
                throw new ServiceException("重复的小区名称");
        }

        CommunityDO updateData = new CommunityDO();
        BeanUtil.copyProperties(communityVO,updateData);
        updateData.setUpdateTime(LocalDateTime.now());
        updateData.setId(communityVO.getId());
        updateById(updateData);
    }


}
