package com.ydg.cloud.lock.utils;

/**
 * 统一校验工具类
 *
 * @author YDG
 */
public class Validator {

    public static void isTrue(boolean expression, RuntimeException re) {
        if (!expression) {
            throw re;
        }
    }

    public static void notNull(Object obj, RuntimeException re) {
        if (obj == null) {
            throw re;
        }
    }


}