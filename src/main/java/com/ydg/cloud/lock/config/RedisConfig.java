package com.ydg.cloud.lock.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author YDG
 * @description
 * @since 2019-03-29
 */
@Slf4j
@Configuration
public class RedisConfig {

    /**
     * SpringSession  需要注意的就是redis需要2.8以上版本，然后开启事件通知，在redis配置文件里面加上
     * notify-keyspace-events Ex
     * Keyspace notifications功能默认是关闭的（默认地，Keyspace 时间通知功能是禁用的，因为它或多或少会使用一些CPU的资源）。
     * 或是使用如下命令：
     * redis-cli config set notify-keyspace-events Egx
     * 如果你的Redis不是你自己维护的，比如你是使用阿里云的Redis数据库，你不能够更改它的配置，那么可以使用如下方法：在applicationContext.xml中配置
     * <util:constant static-field="org.springframework.session.data.redis.config.ConfigureRedisAction.NO_OP"/>
     *
     * @return
     */
    /*@Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }*/

    @Value("${redis.cache.conn.host}")
    @Setter
    private String redisHost;

    @Value("${redis.cache.conn.port}")
    @Setter
    private int redisPort;

    @Value("${redis.cache.conn.password}")
    @Setter
    private String redisPassword;

    @Value("${redis.cache.conn.timeout:5000}")
    @Setter
    private int redisTimeout;

    @Bean
    public JedisPool redisPoolFactory() {
        log.info("开始创建redis连接池：" + redisHost + ":" + redisPort);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(redisTimeout);
        if (StringUtils.isEmpty(redisPassword)) {
            return new JedisPool(jedisPoolConfig, redisHost, redisPort, redisTimeout);
        }
        return new JedisPool(jedisPoolConfig, redisHost, redisPort, redisTimeout, redisPassword);
    }

}
