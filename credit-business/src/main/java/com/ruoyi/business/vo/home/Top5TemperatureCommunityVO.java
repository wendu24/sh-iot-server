package com.ruoyi.business.vo.home;

import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class Top5TemperatureCommunityVO {


    List<RoomDataThirtyDayVO> top5 ;
    List<RoomDataThirtyDayVO> low5 ;

}
