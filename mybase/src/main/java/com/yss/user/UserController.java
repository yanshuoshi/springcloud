package com.yss.user;

import com.yss.common.bean.ApiResult;
import com.yss.common.bean.HttpResult;
import com.yss.common.bean.RequestHeaderContext;
import com.yss.externalcall.ExternalAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shuoshi.yan
 * @description:
 * @date 2020/08/07
 **/
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private ExternalAuthService externalAuthService;

    /**
     * @author:chengyu.duan
     * @description:修改密码
     * @date: 2018/5/4 9:45
     */
    @PostMapping(value = "/login")
    public ApiResult queryUser(HttpServletRequest request,
                               @RequestParam(value = "userName", required = true) String userName,
                               @RequestParam(value = "passWord", required = false) String passWord) {
        ApiResult apiResult = new ApiResult();
        try {
//            String accessToken = RequestHeaderContext.getInstance().getAccesToken();
            HttpResult httpResult = externalAuthService.queryUser(request, userName, passWord);
            if (httpResult.getCode() == 0) {
                apiResult.setSuccess();
            } else {
                apiResult.setMessage(httpResult.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }
}
