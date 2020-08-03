package com.yss.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.yss.common.bean.ApiResult;
import com.yss.common.constant.UrlConstant;
import com.yss.redis.RedisFactory;
import com.yss.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shuoshi.yan
 * @description: 过滤器(token操作)
 * @date 2020/07/31
 **/
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

  @Autowired
  private RedisFactory redisFactory;

  /**
   * 过滤器类型
   * pre 事前
   * routing 路由请求时候调用
   * error 发生错误时候调用
   *
   * @return
   */
  @Override
  public String filterType() {
    return "pre";
  }

  /**
   * 定义filter的顺序，数字越小表示顺序越高，越先执行
   */
  @Override
  public int filterOrder() {
    return 0;
  }

  /**
   * @return
   */
  @Override
  public boolean shouldFilter() {
    return true;
  }

  /**
   * 拦截的具体操作
   * 验证token
   *
   * @return
   */
  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletResponse response = ctx.getResponse();
    HttpServletRequest request = ctx.getRequest();
    response.setHeader("Access-Control-Allow-Origin", "*");//指定哪些域可以请求
    response.setHeader("Access-Control-Allow-Credentials", "true");//是否接收和发送cookies
    response.setHeader("Access-Control-Allow-Methods", "*");//请求方式
    response.setHeader("Access-Control-Max-Age", "3600");//预检缓存时间，前端发送的option请求
    response.setHeader("Access-Control-Allow-Headers", "Origin,Content-Type,accessToken,X-Requested-With,Accept");//自定义请求头字段
    response.setHeader("Access-Control-Expose-Headers", "*");
    String url = request.getRequestURL().toString();
    RedisService redisService = redisFactory.getRedis();
    log.info("request url- - -" + url);
    if (!url.contains("login") && !url.contains("logout") && !url.contains("apidata") && !url.contains("getPic") && !url.endsWith("api")) {
      String accessToken = request.getHeader("accessToken");
      if (StringUtils.isNotEmpty(accessToken)) {
        String userCode = redisService.getUserId(accessToken);
        //登录超时
        if (StringUtils.isEmpty(userCode)) {
          ApiResult result = new ApiResult(20, "登录超时，请重新登录！", null, null);
          ctx.setSendZuulResponse(false); //不进行路由
          ctx.setResponseStatusCode(200);
          try {
            response.setContentType("text/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(result));
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        } else {
          //校验成功
          redisService.validate(accessToken);
          ctx.addZuulRequestHeader("accessToken", accessToken);
          return null;
        }
      } else {
        ApiResult result = new ApiResult(10, UrlConstant.ACCESTOKEN_NULL, null, null);
        ctx.setSendZuulResponse(false); //不进行路由
        ctx.setResponseStatusCode(200);
        try {
          response.setContentType("text/json;charset=UTF-8");
          response.getWriter().write(JSONObject.toJSONString((result)));
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }
    return null;
  }
}
