package com.yss.redis;

import com.yss.utils.SerializeUtil;
import com.yss.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * @author shuoshi.yan
 * @Description:
 * @date 2020/08/03
 */
@Service
//@RefreshScope
@Slf4j
public class RedisServiceImpl implements RedisService {

  @Value("${spring.redis.host}")
  private String ip;
  @Value("${spring.redis.port}")
  private Integer port;
  @Value("${spring.redis.password}")
  private String password;
  /**
   * 可用连接实例的最大数目，默认值为8
   */
  @Value("${spring.redis.maxActive}")
  private Integer maxActive;
  /**
   * 控制最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8
   */
  @Value("${spring.redis.maxIdle}")
  private Integer maxIdle;
  /**
   * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。
   */
  @Value("${spring.redis.maxWait}")
  private Long maxWait;
  /**
   * 当该属性为true时，在调用borrowObject方法返回连接前，会调用validated方法进行校验。若校验失败，连接会从连接池中移除并销毁。同时会尝试重新借一个新的连接对象
   */
  @Value("${spring.redis.testOnBorrow}")
  private Boolean testOnBorrow;
  /**
   * 通过returnObject方法规则连接到连接池时，会调用validateObject方法，校验当前连接，校验不通过，销毁给连接。
   */
  @Value("${spring.redis.testOnReturn}")
  private Boolean testOnReturn;
  @Value("${spring.redis.timeOut}")
  /** 连接超时时间 */
  private Integer timeOut;
  /**
   * 有效时间
   */
  @Value("${spring.redis.redisExpire}")
  private int redisExpire;

  private static final String VIRTUAL_COURSE_PREX = "lc_vc_";

  private static final String RETURN_OK = "OK";

  /**
   * 非切片链接池
   */
  private JedisPool jedisPool;

  /**
   * 初始化
   */
  private void poolInit() {
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
      }
    }
    return jedis;
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
      }
    }
    return flag;

  }

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
        if (jedis == null || !jedis.exists(userId)) {
          // token生产规则自定义ֵ
          jedis.setex(accessToken, redisExpire, userId);
          jedis.setex(userId, redisExpire, accessToken);
        } else {
          //销毁之前的token
          String token = jedis.get("userId");
          if (StringUtils.isNotEmpty(token)) {
            if (jedis == null || !jedis.exists(token)) {
              jedis.del(token);
            }
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();

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
        if (jedis == null || !jedis.exists(token)) {
        } else {
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
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
          if (StringUtils.isNotEmpty(userId)) {
            jedis.del(userId);
          }
          jedis.del(token);
        }
      }
    } catch (Exception e) {
      log.error("redis连接异常");
      e.printStackTrace();
      // 释放jedis对象
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
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
      jedisPool.close();
    } finally {
      // 返还连接池
      if (jedis != null && jedisPool != null) {
        jedisPool.close();
      }
    }
    return userId;
  }

  @Override
  public String getSupportType() {
    return "redis";
  }

  private String buildKey(String key) {
    return VIRTUAL_COURSE_PREX + key;
  }
}
