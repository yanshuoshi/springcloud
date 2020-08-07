package com.yss.externalcall;

import com.yss.common.bean.HttpResult;
import com.yss.externalcall.impl.ExternalAuthServiceHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author shuoshi.yan
 * @className:
 * @description:字典项外部接口
 * @date
 **/
@FeignClient(value = "myauth",fallback = ExternalAuthServiceHystrix.class)
public interface ExternalAuthService {

    /**
     * @author:shuoshi.yan
     * @description:
     */
    @GetMapping(value = "/myauth/user/login")
    HttpResult queryUser(HttpServletRequest request,
                         @RequestParam(value = "userName", required = true) String userName,
                         @RequestParam(value = "passWord", required = false) String passWord);

}
