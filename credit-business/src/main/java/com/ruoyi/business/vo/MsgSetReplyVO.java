package com.ruoyi.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MsgSetReplyVO {

    /**
     * 主键ID
     */
    private Long id;



    /**
     * 消息ID
     */
    private Integer mid;

    /**
     * 命令码 (十进制)
     */
    private Integer cmdCode;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 设备序列号
     */
    private String deviceSn;

    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyTime;

    /**
     * @see com.ruoyi.business.iot.common.constant.ReadWriteEnum
     * 读写
     */
    private Integer readOrWrite;

    /**
     * 下发
     */
    private String publishData;


    private String replyData;



    /**
     * 以下是查询参数
     */


    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishEndTime;

    private Boolean hasReply;
}
