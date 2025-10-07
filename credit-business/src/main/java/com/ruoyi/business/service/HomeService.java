package com.ruoyi.business.service;

import com.ruoyi.business.domain.StatHourDO;
import com.ruoyi.business.vo.home.*;

import java.util.List;

public interface HomeService {


    public OverviewVO overview(HomeQueryVO homeQueryVO);

    public List<RoomDataThirtyDayVO> roomDataThirtyDays(HomeQueryVO homeQueryVO);

    public Top5TemperatureCommunityVO top5TemperatureCommunity(HomeQueryVO homeQueryVO);

    public List<StatHourDO> scatterChart(HomeQueryVO homeQueryVO);


}
