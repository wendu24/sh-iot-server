package com.ruoyi.business.iot.common.vo.down;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DtuDownDataVO {


    private LocalDateTime publishTime;

    List<CommonDownDataVO> dataVOList;


}
