package com.yss.redis;

import java.util.List;
import java.util.Map;


/**
 * @author shuoshi.yan
 * @Description: redis接口
 * @date 2020/08/04
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
    void delString(String key);

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
     * 缓存List类型
     *
     * @param key
     * @param list
     */
    <T> void setList(String key, List<T> list);

    /**
     * 获取List类型
     *
     * @param key
     */
    <T> List<T> getList(String key);

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

    /**
     * @author:shuoshi.yan
     * @description:查询缓存数据量
     */
    long getValue(String key);
    
    
    /**
     * @author:shuoshi.yan
     * @description:获取分布式锁
    */
    String setLock(String lockKey, String requestId, int expireTime);


    /**
     * @author:shuoshi.yan
     * @description:释放分布式锁
    */
    Object  releaseLock(String lockKey, String requestId);
    
    /**
     * @author:shuoshi.yan
     * @description:获取字符串
    */
    String getString(String key);

    /**
     * @author:shuoshi.yan
     * @description:批量删除
     */
    void dels(String pattern );
    /**
     * @author:shuoshi.yan
     * @description:根据key查询hash
    */
    Map<String, String>  getHash(String key);

    /**
     * @author:shuoshi.yan
     * @description:哈希-存值
     */
    void hashSetMapString(String key, Map<String, String> map);

    /**
     * @author:shuoshi.yan
     * @description:哈希-设置一个键值对(不存在，则创建；否则，修改)
     */
    void hashSetOneString(String key, String field, String value);

    /**
     * @author:shuoshi.yan
     * @description:哈希-判断该map中是否存在该field
     */
    boolean hashExistFieldString(String key, String field);

    /**
     * @author:shuoshi.yan
     * @description:哈希-删除map中指定key
     */
    void hashDelFieldString(String key, String field);

    /**
     * @author:shuoshi.yan
     * @description:哈希-获取指定map-key的值
     */
    String hashGetValueString(String key, String field);

}
