package cn.gzcc.membook.utils;

import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 * 自增id，每调用一次自动+1
 * 使用integer原子操作
 * */
public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getId() {
        return c.incrementAndGet();
    }
}
