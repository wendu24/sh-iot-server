package com.ruoyi.business.controller;

import com.ruoyi.business.service.CommunityService;
import com.ruoyi.business.vo.CommunityVO;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wjx/community")
public class CommunityController {


    @Autowired
    private CommunityService communityService;


    @RequestMapping("/add")
    public AjaxResult add(@RequestBody @Validated CommunityVO communityVO){
        communityService.add(communityVO);
        return AjaxResult.success();
    }


}
