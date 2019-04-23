package com.ydg.cloud.lock.service;

import com.ydg.cloud.lock.annotation.Lock;
import com.ydg.cloud.lock.dto.TestLockDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author YDG
 * @description
 * @since 2019-04-22
 */
@Slf4j
@Service
public class TestLockService {

    @Lock(keyName = "id")
    public void testLock(TestLockDTO testLockDTO, TestLockDTO xxx1, TestLockDTO xxx2, TestLockDTO xxx3, Long id) {
        try {
            Thread.sleep(1000L);
            log.info("当前线程获取到锁，当前线程名称是:{}", Thread.currentThread().getName() + ",执行业务完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
