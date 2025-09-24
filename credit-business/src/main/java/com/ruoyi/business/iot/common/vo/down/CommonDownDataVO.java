package com.ruoyi.business.iot.common.vo.down;

import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommonDownDataVO {

    /**
     * 命令的序号
     */
    private Short mid;
    /**
     * 读写标识
     */
    private ReadWriteEnum readWriteFlag;
    /**
     * 数据 , 如果是我读数据,这个字段为空
     */
    private Short data;


}
