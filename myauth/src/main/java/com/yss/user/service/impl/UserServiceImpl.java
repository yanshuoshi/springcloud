package com.yss.user.service.impl;

import com.yss.common.bean.ReturnClass;
import com.yss.common.eum.IsDelete;
import com.yss.redis.RedisFactory;
import com.yss.redis.RedisService;
import com.yss.user.constant.UserConstant;
import com.yss.user.mapper.UserMapper;
import com.yss.user.pojo.UserPo;
import com.yss.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author shuoshi.yan
 * @description:用户服务层
 * @date 2020/08/06
 **/
@Service
@Slf4j
@RefreshScope
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisFactory redisFactory;

    private static final String TRUE_STRING = "true";

    @Value("${spring.mac.flag:false}")
    private String macFlag;


    @Override
    public ReturnClass<Map<String, Object>> queryUser(String userName, String passWord) {
        ReturnClass<Map<String, Object>> returnClass = new ReturnClass<>();
        Map<String, Object> map = new HashMap<>();

        //用户是否存在
        Integer state = userMapper.queryIsHavaUser(userName);
        RedisService redisService = redisFactory.getRedis();

        String code = redisService.login("usercode");
        map.put("accessToken", code);

        returnClass.setSuccess(true);
        returnClass.setData(map);
        return returnClass;
    }

}
