package com.ruoyi.business.iot.common.util;

import com.ruoyi.common.core.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class MidGenerator {

    @Autowired
    RedisCache redisCache;

    /**
     * 范围 0~ 32,767
     * 自增,保证每天不同deviceSn  mid不重复
     * @param sn
     * @return
     */
    public Short generatorMid(String sn){
        // 使用字符串的哈希码作为随机数生成器的种子
        int dayOfMonth = LocalDate.now().getDayOfMonth();

        String key = sn + "_" + dayOfMonth;
        Long midL = redisCache.redisTemplate.opsForValue().increment(key);
        redisCache.expire(key,1, TimeUnit.DAYS);
        return midL.shortValue();
    }
}
