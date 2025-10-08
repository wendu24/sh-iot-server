package com.ruoyi.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.vo.MsgSetReplyVO;

public interface MsgSetReplyService extends IService<MsgSetReplyDO> {

    public Page<MsgSetReplyVO> list(MsgSetReplyVO msgSetReplyVO);
}
