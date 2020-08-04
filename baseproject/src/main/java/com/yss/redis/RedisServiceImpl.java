package com.yss.redis;

import com.alibaba.fastjson.JSONObject;
import com.yss.util.SerializeUtil;
import com.yss.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author shuoshi.yan
 * @Description: TODO
 * @date 2020/08/04
 */
@Service
@RefreshScope
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Value("${spring.redis.host}")
    private String ip;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.maxActive}")
    private Integer maxActive;
    @Value("${spring.redis.maxIdle}")
    private Integer maxIdle;
    @Value("${spring.redis.maxWait}")
    private Long maxWait;
    @Value("${spring.redis.testOnBorrow}")
    private Boolean testOnBorrow;
    @Value("${spring.redis.testOnReturn}")
    private Boolean testOnReturn;
    @Value("${spring.redis.timeOut}")
    private Integer timeOut;
    @Value("${spring.redis.redisExpire}")
    private int redisExpire;
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private static final String VIRTUAL_COURSE_PREX = "lc_vc_";
    @Value("${srping.redis.type:redis}")
    private String redisType;
    /**
     * 非切片链接池
     */
    private JedisPool jedisPool;

    /**
     * 在多线程环境同步初始化
     */
    private void poolInit() {
        if (!"redis".equals(redisType)) {
            return;
        }
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(maxActive);
            config.setMaxIdle(maxIdle);
            config.setMaxWaitMillis(maxWait);
            config.setTestOnBorrow(testOnBorrow);
            config.setTestOnReturn(testOnReturn);
            jedisPool = new JedisPool(config, ip, port, timeOut, password);
            log.info("初始化redis连接池");
        }

    }

    /**
     * 非切片客户端链接 同步获取非切片Jedis实例
     *
     * @return Jedis
     */
    @SuppressWarnings("deprecation")
    private synchronized Jedis getJedis() {
        if (jedisPool == null) {
            poolInit();
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
        return jedis;

    }


    private String buildKey(String key) {
        return VIRTUAL_COURSE_PREX + key;
    }

    @Override
    public void set(String key, String param) {
        String bKey = buildKey(key);
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.set(bKey.getBytes(), SerializeUtil.serialize(param));
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }

    }

    @Override
    public void setWithExpireTime(String key, String value, int expireTime) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.setex(buildKey(key), expireTime, value);
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }

    }

    @Override
    public void setWithExpireTime(String key, String value) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                log.info("获取redis连接");
                jedis.setex(buildKey(key), redisExpire, value);
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }

    }

    @Override
    public String get(String key) {
        String bKey = buildKey(key);
        String retru = null;
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                log.info("获取redis连接");
                if (jedis == null || !jedis.exists(bKey.getBytes())) {
                    return null;
                }
                byte[] in = jedis.get(bKey.getBytes());
                retru = SerializeUtil.unserialize(in).toString();

            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
        return retru;

    }

    @Override
    public void del(String key) {
        String bKey = buildKey(key);
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(bKey.getBytes())) {
                    jedis.del(bKey.getBytes());
                }

            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }


    }

    @Override
    public void delString(String key) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key)) {
                    jedis.del(key);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public Boolean exists(String key) {
        String bKey = buildKey(key);
        Jedis jedis = null;
        boolean flag = false;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(bKey.getBytes())) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }
        }
        return flag;

    }


    /**
     * @param key
     * @param bean
     */
    @Override
    public <T> void setBean(String key, Object bean) {
        String bKey = buildKey(key);
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.set(bKey.getBytes(), SerializeUtil.serialize(bean));
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }

    }

    @Override
    public <T> T getBean(String key) {
        String bKey = buildKey(key);
        T bean = null;
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis == null || !jedis.exists(bKey.getBytes())) {
                    return null;
                }
                byte[] in = jedis.get(bKey.getBytes());
                bean = (T) SerializeUtil.unserialize(in);
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
        return bean;

    }

    /**
     * @param key
     * @param list
     */
    @Override
    public <T> void setList(String key, List<T> list) {
        String bKey = buildKey(key);
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.set(bKey.getBytes(), SerializeUtil.serialize(list));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }

        }

    }

    /**
     * @param key
     * @return list
     */
    @Override
    public <T> List<T> getList(String key) {
        Jedis jedis = null;
        String bKey = buildKey(key);
        List<T> list = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis == null || !jedis.exists(bKey.getBytes())) {
                    return null;
                }
                byte[] in = jedis.get(bKey.getBytes());
                list = (List<T>) SerializeUtil.unserialize(in);
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hc.redis.dao.RedisDao#login(java.lang.String, int)
     */
    @Override
    public String login(String userId) {
        log.info("用户登录");
        String accessToken = UUIDUtil.creatUUID();
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && !jedis.exists(userId)) {
                    // token生产规则自定义ֵ
                    jedis.setex(accessToken, redisExpire, userId);
                    jedis.setex(userId, redisExpire, accessToken);
                } else if (jedis != null && jedis.exists(userId)) {
                    //销毁之前的token
                    String token = jedis.get(userId);
                    if (StringUtils.isNotEmpty(token)) {
                        jedis.del(token);
                    }
                    jedis.del(userId);
                    //重新生成token
                    jedis.setex(accessToken, redisExpire, userId);
                    jedis.setex(userId, redisExpire, accessToken);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }
        }
        return accessToken;
    }

    @Override
    public void validate(String token) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(token)) {
                    //重新设置有效时间
                    String userId = this.getUserId(token);
                    jedis.expire(token, redisExpire);
                    jedis.expire(userId, redisExpire);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }
        }

    }

    @Override
    public void logout(String token) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(token)) {
                    String userId = this.getUserId(token);
                    jedis.del(userId);
                    jedis.del(token);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }

    }

    @Override
    public String getUserId(String token) {
        Jedis jedis = null;
        String userId = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(token)) {
                    userId = jedis.get(token);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
        return userId;
    }

    @Override
    public String getSupportType() {
        return "redis";
    }

    @Override
    public long getValue(String key) {
        Jedis jedis = null;
        String count = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key)) {
                    count = jedis.get(key);
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
        return Long.valueOf(count);
    }

    @Override
    public String setLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = null;
        String result = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null) {
                    result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                } else {
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    @Override
    public Object releaseLock(String lockKey, String requestId) {
        Jedis jedis = null;
        Object result = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                if (jedis != null) {
                    result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
                } else {
                    return result;
                }

            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    @Override
    public String getString(String key) {
        Jedis jedis = null;
        String count = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key)) {
                    count = jedis.get(key);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return count;
    }

    /**
     * @author:shuoshi.yan
     * @description:批量删除keys
     * @date: 2018/10/17 15:57
     */
    @Override
    public void dels(String pattern) {
        String bKey = buildKey(pattern);
        Jedis jedis = null;
        try {
            Set<String> keys = null;
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && StringUtils.isNotEmpty(pattern)) {
                    keys = jedis.keys(bKey + "*");
                    if (keys != null) {
                        for (String key : keys) {
                            System.err.println(key);
                            jedis.del(key.getBytes());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }

        }
    }

    /**
     * @param key
     * @author:shuoshi.yan
     * @description:根据key查询hash
     * @date: 2018/12/5 18:26
     * @param: null
     * @return: null
     */
    @Override
    public Map<String, String> getHash(String key) {
        Jedis jedis = null;
        String count = null;
        log.error(">>>");
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key)) {
                    Map<String, String> map = jedis.hgetAll(key);
                    log.info(">>>>" + JSONObject.toJSONString(map));
                    return map;
                } else {
                    log.info(">>>>key 不存在");
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return null;
    }

    @Override
    public void hashSetMapString(String key, Map<String, String> map) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.hmset(key, map);
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);

            }
        }
    }

    @Override
    public void hashSetOneString(String key, String field, String value) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                jedis.hset(key, field, value);
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public boolean hashExistFieldString(String key, String field) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key)) {
                    flag = jedis.hexists(key, field);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return flag;
    }

    @Override
    public void hashDelFieldString(String key, String field) {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key) && StringUtils.isNotEmpty(field)) {
                    jedis.hdel(key, field);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public String hashGetValueString(String key, String field) {
        Jedis jedis = null;
        String value = "";
        try {
            if (jedisPool == null) {
                poolInit();
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null && jedis.exists(key)) {
                    value = jedis.hget(key, field);
                }
            }
        } catch (Exception e) {
            log.error("redis连接异常");
            e.printStackTrace();
            // 释放jedis对象
            jedisPool.returnBrokenResource(jedis);
        } finally {
            // 返还连接池
            if (jedis != null && jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return value;
    }
}
