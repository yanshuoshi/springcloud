package com.yss.user.controller;

import com.yss.common.bean.ApiResult;
import com.yss.common.bean.RequestHeaderContext;
import com.yss.common.bean.ReturnClass;
import com.yss.redis.RedisFactory;
import com.yss.redis.RedisService;
import com.yss.user.constant.UserConstant;
import com.yss.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shuoshi.yan
 * @package:com.yss.user.controller
 * @className:
 * @description:用户控制层
 * @date 2018-02-08 16:14
 **/
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisFactory redisFactory;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult queryUser(HttpServletRequest request, @RequestParam(value = "userName", required = true) String userName, @RequestParam(value = "passWord", required = false) String passWord, @RequestParam(value = "sysFunction", required = false) String sysFunction, @RequestParam(value = "sysChildFunction", required = false) String sysChildFunction, @RequestParam(value = "auth", required = false) String auth, @RequestParam(value = "mac", required = false) String mac) {
        ApiResult apiResult = new ApiResult();
        try {

            RedisService redisService = redisFactory.getRedis();
            //查询用户

            ReturnClass<Map<String, Object>> returnClass = userService.queryUser(userName, passWord, auth, mac);
            apiResult.setMessage(returnClass.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }
}
