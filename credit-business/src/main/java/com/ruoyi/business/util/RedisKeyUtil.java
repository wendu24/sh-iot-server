package com.ruoyi.business.util;

import java.util.Date;

public class RedisKeyUtil {


    public static String udpMsgCacheKey(String deviceSn){
        return "UDP_MSG_" + deviceSn;
    }
}
