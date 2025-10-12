package com.ruoyi.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UdpManualDownVO {


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    List<CommonDownDataVO> dataVOList;


    private String ip;

    private int port;
}
