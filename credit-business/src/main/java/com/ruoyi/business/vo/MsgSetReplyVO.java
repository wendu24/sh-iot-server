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
     * 主题
     */
    private String topic;

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
     * 消息体 (json数据)
     */
    private String msgBody;

    /**
     * 加密消息体 (八进制字符串)
     */
    private String msgEncryBody;

    /**
     * 回复体 (json数据)
     */
    private String replyBody;

    /**
     * 加密回复体 (八进制字符串)
     */
    private String replyEncryBody;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


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
