package com.ydg.cloud.lock.lock;

import com.ydg.cloud.lock.constants.Constant;
import com.ydg.cloud.lock.exception.LockException;
import com.ydg.cloud.lock.utils.Validator;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Slf4j
@Component
@Scope("prototype")
public class RedisDistributeLock extends AbstractLock {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultRedisScript<Boolean> redisUnLockScript;

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
        Validator.isTrue(result, new LockException("try lock timeout , key: " + getLockKey()));
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
        Validator.isTrue(mills <= maxTryLockTimeOut,
                new LockException("try lock timeout longer than maxTryLockTimeOut"));
        return lock(mills);
    }

    @Override
    public void unlock() {
        if (StringUtils.isBlank(key)) {
            throw new LockException("key must not blank");
        }
        this.unlock(this.getLockKey());
    }

    /**
     * 在指定的时间内进行进行获取锁操作 阻塞
     *
     * @param tryLockTimeOut
     * @return
     */
    private boolean lock(long tryLockTimeOut) {
        long start = System.currentTimeMillis();
        requestId = UUID.randomUUID().toString();
        boolean result = false;
        String lockKey = getLockKey();
        //如果花费时间小于获取锁最大超时时间,并且没有获取锁成功,不断进行获取锁操作
        while ((System.currentTimeMillis() - start) < tryLockTimeOut && !result) {
            result = this.lock(lockKey, defaultLockTimeOut);
            //如果获取失败,那么就进行休眠,等待重新获取锁
            log.info("current thread : {}, current key: {} , get lock result : {}", Thread.currentThread().getName(),
                    lockKey, result);
            if (!result) {
                try {
                    Thread.sleep(tryLockInterval);
                } catch (InterruptedException e) {
                    log.error("current thread : {} , try lock thread sleep exception", Thread.currentThread().getName(),
                            e);
                    throw new LockException("try lock thread sleep exception");
                }
            }
        }
        return result;
    }

    private String getLockKey() {
        if (StringUtils.isBlank(sourceType)) {
            sourceType = Constant.CUSTOMIZE_SOURCE_TYPE;
        }
        return PATH + sourceType + key;
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
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.MILLISECONDS);
        if (result == null || !result) {
            return false;
        }
        log.info("current thread : {} , lock success , current key : {} , requestId : {} , expireTime : {} ms",
                Thread.currentThread().getName(), lockKey, requestId, expireTime);
        return true;
    }

    /**
     * 释放锁
     *
     * @param lockKey 资源key
     * @return 返回成功还是失败
     */
    private boolean unlock(String lockKey) {
        Boolean result = (Boolean) redisTemplate
                .execute(redisUnLockScript, Collections.singletonList(lockKey), requestId);
        if (result == null || !result) {
            log.info("current thread : {} , release lock fail , current key: {} , requestId : {}",
                    Thread.currentThread().getName(), lockKey, requestId);
            return false;
        }
        log.info("current thread : {} , release lock success , current key: {} , requestId : {}",
                Thread.currentThread().getName(), lockKey, requestId);
        return true;
    }

}
