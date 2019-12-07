package cn.gzcc.membook.utils;

import java.util.concurrent.atomic.AtomicInteger;


public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getId() {
        return c.incrementAndGet();
    }
}
