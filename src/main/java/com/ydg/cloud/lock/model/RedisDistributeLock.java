package com.ydg.cloud.lock.model;

import com.ydg.cloud.lock.enums.SourceTypeEnum;
import com.ydg.cloud.lock.exception.LockException;
import com.ydg.cloud.lock.utils.Assert;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author YDG
 * @Company qcsz
 * @description
 * @since 2019-03-29
 */
@Slf4j
@Component
@Scope("prototype")
public class RedisDistributeLock extends AbstractLock {

    /**
     * redis连接池
     */
    @Autowired
    private JedisPool jedisPool;

    /**
     * 操作成功返回值
     */
    private final static String OK = "OK";

    /**
     * 不存在则创建
     */
    private static final String SET_IF_NOT_EXIST = "NX";

    /**
     * 创建key的同时设置它的过期时间
     */
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * 每次生成获取锁的同时，记录请求id（防止分布式，key被其他服务器删除）
     */
    private String requestId;

    /**
     * 资源key
     */
    private static final String PATH = "lock:";

    @Override
    public void lock() {
        boolean result = this.lock(maxTryLockTimeOut);
        Assert.isTrue(result, new LockException("try lock timeout, key: " + getLockKey()));
    }

    @Override
    public boolean tryLock() {
        if (StringUtils.isBlank(key)) {
            throw new LockException("key must not blank");
        }
        return lock(getLockKey(), defaultLockTimeOut);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            throw new LockException("key must not blank");
        }
        long mills = unit.toMillis(time);
        Assert.isTrue(mills <= maxTryLockTimeOut, new LockException("try lock timeout longer than maxTryLockTimeOut"));
        return lock(mills);
    }

    @Override
    public void unlock() {
        if (StringUtils.isBlank(key)) {
            throw new LockException("key must not blank");
        }
        this.unlock(this.getLockKey());
    }

    private boolean lock(long tryLockTimeOut) {
        long start = System.currentTimeMillis();
        requestId = UUID.randomUUID().toString();
        boolean result = false;
        String lockKey = getLockKey();
        //如果花费时间小于获取锁最大超时时间,并且没有获取锁成功,不断进行获取锁操作
        while ((System.currentTimeMillis() - start) < tryLockTimeOut && !result) {
            log.info("try lock, current key: " + lockKey);
            result = this.lock(lockKey, defaultLockTimeOut);
            //如果获取失败,那么就进行休眠,等待重新获取锁
            if (!result) {
                try {
                    Thread.sleep(tryLockInterval);
                } catch (InterruptedException e) {
                    log.error("try lock thread sleep exception", e);
                    throw new LockException("try lock thread sleep exception");
                }
            }
        }
        return result;
    }

    private String getLockKey() {
        if (type == null) {
            type = SourceTypeEnum.CUSTOMIZE;
        }
        return PATH + type.getPre() + key;
    }

    /**
     * 解决高并发问题，获取锁机制
     * true:表示获取成功，可执行接下来操作
     * false:表示获取失败，锁已被占用，需终止当前操作或者等待锁释放
     *
     * @param lockKey    资源
     * @param expireTime 过期时间
     * @return 返回成功还是失败
     */
    private boolean lock(String lockKey, int expireTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            //redis锁最核心的就是下面的这个操作
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (OK.equals(result)) {
                log.info("lock success, current key: {}, requestId: {}, expireTime: {} ms", lockKey, requestId,
                        expireTime);
                return true;
            }
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param key 资源
     * @return 返回成功还是失败
     */
    private boolean unlock(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));

            if (OK.equals(result)) {
                log.info("release lock success, current key: {}, requestId: {}", getLockKey(), requestId);
                return true;
            }
            log.info("release lock success, current key: {}, requestId: {}", getLockKey(), requestId);
            return false;
        }
    }

}
