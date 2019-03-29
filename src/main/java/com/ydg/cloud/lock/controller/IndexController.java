package com.ydg.cloud.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YDG
 * @Company qcsz
 * @description
 * @since 2019-03-29
 */
@Slf4j
@RestController
public class IndexController {

    /**
     * 主页
     *
     * @return
     */
    @GetMapping("index")
    public String index() {
        return "我的分布式锁服务";
    }
}
