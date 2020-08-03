package com.yss.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shuoshi.yan
 * @description:redis工厂类
 * @date 2020/08/03
 **/
@Component
public class RedisFactory {

  @Autowired
  private List<RedisService> redisServiceFactorys;

  @Value("${srping.redis.type:redis}")
  private String redisType;

  public RedisService getRedis() {
    RedisService brandFactory = null;
    for (RedisService tmpFactory : redisServiceFactorys) {
      if (tmpFactory.getSupportType().equals(redisType)) {
        brandFactory = tmpFactory;
        break;
      }
    }
    return brandFactory;
  }
}
