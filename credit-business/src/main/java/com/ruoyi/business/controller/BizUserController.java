package com.ruoyi.business.controller;

import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.vo.BizUserVO;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/biz/user")
public class BizUserController {

    @Autowired
    BizUserService bizUserService;

    @RequestMapping("/user-info")
    public AjaxResult userInfo(@RequestBody BizUserVO bizUserVO){
        return AjaxResult.success(bizUserService.getById(bizUserVO.getId()));
    }

}
