package com.ruoyi.business.iot.common.vo.down;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class CommonDownDataVO {

    @NotBlank(message = "SN不能为空")
    private String deviceSn;

    /**
     * 命令的序号
     */
    private Short mid;
    /**
     * 读写标识
     */
    @NotNull(message = "读写标识不能为空")
    private Integer readWriteFlag;
    /**
     * 十进制
     */
    @NotNull(message = "cmd不能为空")
    private byte cmdCode;
    /**
     * 数据 , 如果是我读数据,这个字段为空
     */
    private Long data;

    /**
     * 数据,存放字符类型的数据
     */
    private String dataStr;


    @Tolerate
    public CommonDownDataVO() {
    }
}
