package com.ruoyi.business.iot.udp;


import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储sn和客户端映射
 */
public class DeviceSessionManager {
    // 保存 SN -> 设备地址
    private static final ConcurrentHashMap<String, InetSocketAddress> DEVICE_MAP = new ConcurrentHashMap<>();

    public static void updateDevice(String sn, InetSocketAddress address) {
        DEVICE_MAP.put(sn, address);
    }

    public static InetSocketAddress getDeviceAddress(String sn) {
        return DEVICE_MAP.get(sn);
    }

    public static void removeDevice(String sn) {
        DEVICE_MAP.remove(sn);
    }

    public static boolean contains(String sn) {
        return DEVICE_MAP.containsKey(sn);
    }
}