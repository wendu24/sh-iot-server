package com.ruoyi.business.vo;

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
    private LocalDateTime publishTime;

    /**
     * 设备序列号
     */
    private String deviceSn;

    /**
     * 回复时间
     */
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

    private LocalDateTime publishStartTime;

    private LocalDateTime publishEndTime;

    private Boolean hasReply;
}
