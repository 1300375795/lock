package com.ydg.cloud.lock.enums;

import lombok.Getter;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Getter
public enum SourceTypeEnum {

    /**
     * 锁用户
     */
    USER("0001", "lock_user_", "锁用户"),

    /**
     * 锁订单
     */
    ORDER("0002", "lock_order_", "锁订单"),

    /**
     * 锁方法
     */
    METHOD("0003", "lock_method_", "锁方法"),

    /**
     * 自定义锁
     */
    CUSTOMIZE("0004", "lock_customize_", "自定义锁"),

    ;

    private String code;
    private String pre;
    private String remark;

    SourceTypeEnum(String code, String pre, String remark) {
        this.code = code;
        this.pre = pre;
        this.remark = remark;
    }

}
