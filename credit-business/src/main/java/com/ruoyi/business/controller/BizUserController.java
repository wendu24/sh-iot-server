package com.ruoyi.business.controller;

import com.ruoyi.business.iot.MqttService;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.vo.BizUserVO;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/biz/user")
public class BizUserController {

    @Autowired
    BizUserService bizUserService;

    @Autowired
    private MqttService mqttService;

    @RequestMapping("/user-info")
    public AjaxResult userInfo(@RequestBody BizUserVO bizUserVO){
        return AjaxResult.success(bizUserService.getById(bizUserVO.getId()));
    }


    @RequestMapping("/publish-msg")
    public AjaxResult publishMsg(@RequestBody DtuDownDataVO dtuDownDataVO){
        String topicDeviceSn = "105110042509083201";
        try {
            mqttService.publish(topicDeviceSn, dtuDownDataVO);
            return AjaxResult.success("消息发布成功");
        } catch (Exception e) {
            log.error("发布消息出错啦,",e);
            return AjaxResult.error(e.getMessage());
        }
    }

}
