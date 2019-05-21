package com.ydg.cloud.lock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Slf4j
@Configuration
public class RedisUnLockScriptConfig {

    @Bean
    public DefaultRedisScript<Boolean> redisUnLockScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptText(
                "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        return redisScript;
    }

}
