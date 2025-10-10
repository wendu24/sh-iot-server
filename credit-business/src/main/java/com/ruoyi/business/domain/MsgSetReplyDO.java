package com.ruoyi.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sh_msg_set_reply")
public class MsgSetReplyDO {

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

    private Long communityId;

    private String communityName;

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


    public String msgKey (){
        return deviceSn + "_" + mid;
    }
}
