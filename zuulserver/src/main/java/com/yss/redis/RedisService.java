package com.yss.redis;

/**
 * @author shuoshi.yan
 * @Description: redis接口
 * @date 2020/08/03
 */
public interface RedisService {

  /**
   * 设置缓存
   *
   * @param key   缓存key
   * @param value 缓存value
   */
  void set(String key, String value);

  /**
   * 设置缓存，并且自己指定过期时间
   *
   * @param key
   * @param value
   * @param expireTime 过期时间
   */
  void setWithExpireTime(String key, String value, int expireTime);

  /**
   * 设置缓存，并且由配置文件指定过期时间
   *
   * @param key
   * @param value
   */
  void setWithExpireTime(String key, String value);

  /**
   * 获取指定key的缓存
   *
   * @param key
   */
  String get(String key);

  /**
   * 删除指定key的缓存
   *
   * @param key
   */
  void del(String key);

  /**
   * 判断key是否存在
   *
   * @param key
   */
  Boolean exists(String key);

  /**
   * 缓存Bean类型
   *
   * @param key
   * @param bean
   */
  <T> void setBean(String key, Object bean);

  /**
   * 获取Bean类型
   *
   * @param key
   */
  <T> T getBean(String key);

  /**
   * 用户登录
   *
   * @param userCode
   */
  String login(String userCode);

  /**
   * 用户校验
   *
   * @param accessToken
   */
  void validate(String accessToken);

  /**
   * 用户退出
   *
   * @param accessToken
   */
  void logout(String accessToken);

  /**
   * 获取用户code
   *
   * @param accessToken
   */
  String getUserId(String accessToken);

  String getSupportType();

}
