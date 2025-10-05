package com.ruoyi.business.service;

import com.ruoyi.business.vo.home.HomeQueryVO;
import com.ruoyi.business.vo.home.OverviewVO;
import com.ruoyi.business.vo.home.RoomDataThirtyDayVO;

import java.util.List;

public interface HomeService {


    public OverviewVO overview(HomeQueryVO homeQueryVO);

    public List<RoomDataThirtyDayVO> roomDataThirtyDays(HomeQueryVO homeQueryVO);



}
