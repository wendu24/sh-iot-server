package com.ruoyi.business.iot.common;

import java.util.Random;

public class MidGenerator {


    public static Short generatorMid(String sn){
        // 使用字符串的哈希码作为随机数生成器的种子
        Random random = new Random(sn.hashCode());
        return (short) (random.nextInt(Short.MAX_VALUE - Short.MIN_VALUE + 1) + Short.MIN_VALUE);
    }
}
