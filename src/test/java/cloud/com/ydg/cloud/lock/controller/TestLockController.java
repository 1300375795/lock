package cloud.com.ydg.cloud.lock.controller;

import cloud.com.ydg.cloud.lock.dto.TestLockDTO;
import cloud.com.ydg.cloud.lock.service.TestLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YDG
 * @description
 * @since 2019-04-23
 */
@Slf4j
@RestController
public class TestLockController {

    /**
     * 线程数量
     */
    private static final int THREAD_NUMBER = 20;

    @Autowired
    private TestLockService service;

    @GetMapping("test")
    public void test() {
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                TestLockDTO testLockDTO = new TestLockDTO();
                testLockDTO.setId(1L);
                testLockDTO.setName("测试下");
                TestLockDTO xxx1 = new TestLockDTO();
                TestLockDTO xxx2 = new TestLockDTO();
                TestLockDTO xxx3 = new TestLockDTO();
                service.testLock(testLockDTO, xxx1, xxx2, xxx3, 1L);
            }).start();
        }
    }
}
