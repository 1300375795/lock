package com.ydg.cloud.lock.service.impl;

import com.ydg.cloud.lock.annotation.Lock;
import com.ydg.cloud.lock.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author YDG
 * @description
 * @since 2019-07-20
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void testLock(Long id) {
        assert id != null;
        TestService testService = applicationContext.getBean(TestService.class);
        testService.testLock2(id);
    }

    @Override
    @Lock(keyName = "id")
    public void testLock2(Long id) {
        System.out.println("需要加锁的id是:" + id);
    }
}
