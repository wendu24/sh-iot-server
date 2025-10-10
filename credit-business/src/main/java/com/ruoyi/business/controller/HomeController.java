package com.ruoyi.business.controller;

import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.HomeService;
import com.ruoyi.business.service.StatHourService;
import com.ruoyi.business.vo.home.HomeQueryVO;
import com.ruoyi.business.vo.home.OverviewVO;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    StatHourService statHourService;

    @Autowired
    HomeService homeService;



    /**
     * 供水水温/回水水温 和 小时的折线图
     */
    @RequestMapping("/waterTemperatureAndHour")
    public void waterTemperatureAndHour(@RequestBody HomeQueryVO homeQueryVO){
        AjaxResult.success(homeService.waterTemperatureAndHour(homeQueryVO));
    }


    /**
     *  总览: 小区数, 设备数(按类型), 故障设备数(按类型). 平均室温, 平均设备开度
     */
    @RequestMapping("/overview")
    public AjaxResult overview(@RequestBody HomeQueryVO homeQueryVO){
        return AjaxResult.success(homeService.overview(homeQueryVO));
    }


    /**
     * 近三十天每小时平均室温 ,平均湿度,
     */
    @RequestMapping("/roomDataThirtyDays")
    public AjaxResult roomDataThirtyDays(@RequestBody HomeQueryVO homeQueryVO){
        return AjaxResult.success(homeService.roomDataThirtyDays(homeQueryVO));
    }

    /**
     * 平均室温较高和较低top5
     */
    @RequestMapping("/top5TemperatureCommunity")
    public AjaxResult top5TemperatureCommunity(@RequestBody HomeQueryVO homeQueryVO){
        return AjaxResult.success(homeService.top5TemperatureCommunity(homeQueryVO));
    }

    /**
     * 散点图
     */
    @RequestMapping("/scatterChart")
    public AjaxResult scatterChart(@RequestBody HomeQueryVO homeQueryVO){
        return AjaxResult.success(homeService.scatterChart(homeQueryVO));
    }




}
