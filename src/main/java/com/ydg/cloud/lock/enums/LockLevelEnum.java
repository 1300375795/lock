package com.ydg.cloud.lock.enums;

import com.ydg.cloud.lock.model.AbstractLock;
import com.ydg.cloud.lock.model.RedisDistributeLock;
import lombok.Getter;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Getter
public enum LockLevelEnum {

    /**
     * 数据库级别
     */
    DATABASE("01", "数据库级别", null),

    /**
     * redis级别
     */
    REDIS("02", "redis级别", RedisDistributeLock.class),

    /**
     * zookeeper级别
     */
    ZOOKEEPER("03", "zookeeper级别", null),
    ;

    private String code;
    private String remark;
    private Class<? extends AbstractLock> clz;

    LockLevelEnum(String code, String remark, Class<? extends AbstractLock> clz) {
        this.code = code;
        this.remark = remark;
        this.clz = clz;
    }

}
