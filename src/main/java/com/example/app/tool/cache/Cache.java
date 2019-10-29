package com.example.app.tool.cache;


import com.example.app.tool.cache.delay.DelayItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/10/30, MarkHuang,new
 * </ul>
 * @since 2018/10/30
 */
@Component
public class Cache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cache.class);
    private Map<K, V> cacheObjectMap = new ConcurrentHashMap<>();
    private DelayQueue<DelayItem<Pair>> delayQueue = new DelayQueue<>();

    public Cache() {
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
        );
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                DelayItem<Pair> delayItem = delayQueue.poll();
                if (delayItem == null) return;
                Pair pair = delayItem.getItem();
                cacheObjectMap.remove(pair.key);
                LOGGER.debug("item in cache is expire, key:{}", pair.key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MILLISECONDS);
    }

    private class Pair {
        private K key;
        private V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        Pair pair = new Pair(key, value);
        final DelayItem<Pair> delayItem = new DelayItem<>(pair, timeout, timeUnit);
        V val = this.cacheObjectMap.put(key, value);
        if (val != null) {
            removeIf(
                    this.delayQueue,
                    innerDelayItem -> delayItem.getItem().getKey().equals(innerDelayItem.getItem().getKey())
            );
        }
        this.delayQueue.put(delayItem);
//        LOGGER.debug("put item in cache, key:{}, expire time:{}min", key, TimeUnit.MINUTES.convert(timeout, timeUnit));
    }

    public V get(K key) {
        return this.cacheObjectMap.get(key);
    }


    private <E extends Delayed> boolean removeIf(DelayQueue<E> delayQueue, Filter<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = delayQueue.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }

    interface Filter<R> {
        boolean test(R r);
    }

}
