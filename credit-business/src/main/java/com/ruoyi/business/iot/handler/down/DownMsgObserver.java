package com.ruoyi.business.iot.handler.down;

import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;

public interface DownMsgObserver {


    public void handle(DtuDownDataVO dtuDownDataVO);
}
