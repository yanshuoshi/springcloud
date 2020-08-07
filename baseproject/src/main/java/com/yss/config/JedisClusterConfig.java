package com.yss.config;

import com.yss.redis.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Shuoshi.yan
 * @description:生成JedisCluster对象
 * @date 2020/08/05
 **/
//@Configuration
public class JedisClusterConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Value("${srping.redis.type:redis}")
    private String redisType;

    /**
     * 注意：
     * 这里返回的JedisCluster是单例的，并且可以直接注入到其他类中去使用
     * @return
     */
    @Bean
    public JedisCluster getJedisCluster() {
        if (!"redisCluster".equals(redisType)){
            return null;
        }
        String[] serverArray = redisProperties.getNodes().split(",");//获取服务器数组(这里要相信自己的输入，所以没有考虑空指针问题)
        Set<HostAndPort> nodes = new HashSet<>();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisProperties.getMaxIdle());
        config.setMaxTotal(redisProperties.getMaxActive());
        config.setTestOnBorrow(redisProperties.getTestOnBorrow());
        config.setMaxIdle(redisProperties.getMaxIdle());
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        return new JedisCluster(nodes,redisProperties.getTimeOut(),5000,redisProperties.getMaxAttempts(),redisProperties.getPassword(),config);
//        return new JedisCluster(nodes,redisProperties.getTimeout());
//        return new JedisCluster(nodes,redisProperties.getTimeout(),redisProperties.getMaxAttempts(),config);
    }

}
