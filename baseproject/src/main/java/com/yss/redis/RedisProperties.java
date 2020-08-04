package com.yss.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author shuoshi.yan
 * @description:redis配置参数类
 * @date 2020/08/04
 **/
@Component
@RefreshScope
@Data
public class RedisProperties {

    /** redis集群节点 */
    @Value("${spring.redis.pool.nodes}")
    private String nodes;
    /** 密码 */
    @Value("${spring.redis.pool.password}")
    private String password;
    /** 连接超时时间 */
    @Value("${spring.redis.pool.timeOut}")
    private int timeOut;
    /** 重连次数 */
    @Value("${spring.redis.pool.maxAttempts}")
    private int maxAttempts;
    /** 可用连接实例的最大数目，默认值为8 */
    @Value("${spring.redis.pool.maxActive}")
    private Integer maxActive;
    /**控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8*/
    @Value("${spring.redis.pool.maxIdle}")
    private Integer maxIdle;
    /**等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。*/
    @Value("${spring.redis.pool.maxWait}")
    private Long maxWait;
    /**#在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；*/
    @Value("${spring.redis.pool.testOnBorrow}")
    private Boolean testOnBorrow;
    /** 有效时间 */
    @Value("${spring.redis.pool.expireSeconds}")
    private int expireSeconds;
    /*返回值的超时时间*/
    @Value("${spring.redis.pool.soTimeOut}")
    private int soTimeOut;
}
