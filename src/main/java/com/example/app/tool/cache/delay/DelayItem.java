package com.example.app.tool.cache.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/10/30, MarkHuang,new
 * </ul>
 * @since 2018/10/30
 */
public class DelayItem<T> implements Delayed {

    private T item;
    private long delay;
    private long expire;

    public DelayItem(T item, long delay, TimeUnit unit) {
        long delayMilliSeconds = TimeUnit.MILLISECONDS.convert(delay, unit);
        this.item = item;
        this.delay = delayMilliSeconds;
        this.expire = System.currentTimeMillis() + delayMilliSeconds;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return this.expire - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (delay - o.getDelay(TimeUnit.MILLISECONDS));
    }

    public T getItem() {
        return item;
    }
}
