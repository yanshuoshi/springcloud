package com.yss.externalcall.impl;

import com.yss.common.bean.HttpResult;
import com.yss.common.constant.CommonConstant;
import com.yss.common.eum.ResultCode;
import com.yss.externalcall.ExternalAuthService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * @author shuoshi.yan
 * @description:
 * @date 2020/08/05
 **/
@Component
public class ExternalAuthServiceHystrix implements ExternalAuthService {

    @Override
    public HttpResult queryUser(HttpServletRequest request,
                                @RequestParam(value = "userName", required = true) String userName,
                                @RequestParam(value = "passWord", required = false) String passWord) {
        HttpResult httpResult = new HttpResult();
        httpResult.setCode(ResultCode.DEF_ERROR.getCode());
        httpResult.setMessage(CommonConstant.EXTERNALL_ERROR);
        return httpResult;
    }

}
