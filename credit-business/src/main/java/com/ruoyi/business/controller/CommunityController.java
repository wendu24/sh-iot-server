package com.ruoyi.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.business.domain.CommunityDO;
import com.ruoyi.business.service.CommunityService;
import com.ruoyi.business.validate.CreateGroup;
import com.ruoyi.business.vo.CommunityVO;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/biz/community")
public class CommunityController {


    @Autowired
    private CommunityService communityService;


    @RequestMapping("/list")
    public Page<CommunityDO> list(@RequestBody CommunityVO communityVO) {
        return communityService.page(communityVO);
    }


    @RequestMapping("/add")
    public AjaxResult add(@RequestBody @Validated(CreateGroup.class) CommunityVO communityVO){
        communityService.add(communityVO);
        return AjaxResult.success();
    }


    @RequestMapping("/edit")
    public AjaxResult edit(@RequestBody @Validated(CreateGroup.class) CommunityVO communityVO){
        communityService.add(communityVO);
        return AjaxResult.success();
    }


    @RequestMapping("/delete")
    public AjaxResult delete(@RequestBody CommunityVO communityVO){
        communityService.removeById(communityVO.getId());
        return AjaxResult.success();
    }





}
