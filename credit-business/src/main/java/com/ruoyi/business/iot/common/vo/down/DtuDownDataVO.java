package com.ruoyi.business.iot.common.vo.down;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.business.iot.common.vo.IotUplinkMsg;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DtuDownDataVO implements IotUplinkMsg {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    List<CommonDownDataVO> dataVOList;

    @Tolerate
    public DtuDownDataVO() {
    }
}
