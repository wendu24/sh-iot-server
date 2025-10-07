package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.CommunityDO;
import com.ruoyi.business.vo.CommunityVO;

public interface CommunityService extends IService<CommunityDO> {

    public Page<CommunityDO> page(CommunityVO communityVO);

    public void add(CommunityVO communityVO);


    public void edit(CommunityVO communityVO);
}
