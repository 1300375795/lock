package com.ydg.cloud.lock.utils;

/**
 * @author YDG
 * @Company qcsz
 * @description
 * @since 2019-03-29
 */
public class Assert {

    public static void isTrue(boolean result, RuntimeException e) {
        if (!result) {
            throw e;
        }
    }
}
