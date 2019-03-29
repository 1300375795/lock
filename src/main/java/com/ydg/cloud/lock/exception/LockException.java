package com.ydg.cloud.lock.exception;

/**
 * @author YDG
 * @Company qcsz
 * @description
 * @since 2019-03-29
 */
public class LockException extends RuntimeException {

    /**
     * @param msg 错误原因
     */
    public LockException(String msg) {
        super(msg);
    }

    public LockException() {
        super();
    }

}
