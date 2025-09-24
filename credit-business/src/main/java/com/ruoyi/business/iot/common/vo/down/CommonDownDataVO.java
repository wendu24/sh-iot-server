package com.ruoyi.business.iot.common.vo.down;

import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommonDownDataVO {

    private String deviceSn;

    /**
     * 命令的序号
     */
    private Short mid;
    /**
     * 读写标识
     */
    private ReadWriteEnum readWriteFlag;

    private CmdEnum cmdEnum;
    /**
     * 数据 , 如果是我读数据,这个字段为空
     */
    private Long data;

    /**
     * 数据,存放字符类型的数据
     */
    private String dataStr;


}
