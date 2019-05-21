package com.ydg.cloud.lock.model;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Setter
@Getter
public abstract class AbstractLock implements Lock {

    /**
     * 资源key
     */
    protected String key;

    /**
     * 资源类型
     */
    protected String sourceType;

    /**
     * 默认锁住时间（10s）
     */
    protected int defaultLockTimeOut = 10000;

    /**
     * 获取锁最大超时时间
     */
    protected long maxTryLockTimeOut;

    /**
     * 每次尝试获取锁的时间间隔（毫秒）
     */
    protected long tryLockInterval = 100;

    /**
     * 直接获取锁（一直等待，直到抛出异常or获取锁），阻塞
     */
    @Override
    public abstract void lock();

    /**
     * 尝试获取锁（要么成功，要么失败），非阻塞
     *
     * @return
     */
    @Override
    public abstract boolean tryLock();

    /**
     * 设置时间内获取锁，半阻塞
     *
     * @param time
     * @param unit
     * @return
     */
    @Override
    public abstract boolean tryLock(long time, TimeUnit unit);

    /**
     * 释放锁
     */
    @Override
    public abstract void unlock();

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void lockInterruptibly() {

    }

}
