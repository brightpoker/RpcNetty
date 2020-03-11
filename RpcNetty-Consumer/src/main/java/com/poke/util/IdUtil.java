package com.poke.util;

/**
 * @ClassName IdUtil
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 3:45 上午
 */
public class IdUtil {
    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static String getId() {
        return String.valueOf(idWorker.nextId());
    }
}
