package com.yss.interceptor;

import com.yss.common.bean.RequestHeaderContext;
import com.yss.common.constant.UrlConstant;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shuoshi.yan
 * @package:com.ganinfo.interceptor
 * @className:RequestHeaderContextInterceptor
 * @description:统一拦截request header
 * @date 2018-02-09 21:41
 **/

public class RequestHeaderContextInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        initHeaderContext(request);
        return super.preHandle(request, response, handler);
    }

    private void initHeaderContext(HttpServletRequest request){
        new RequestHeaderContext.RequestHeaderContextBuild()
                .accesToken(request.getHeader(UrlConstant.TOKEN))
                .bulid();
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        RequestHeaderContext.clean();
        super.postHandle(request, response, handler, modelAndView);
    }
}
