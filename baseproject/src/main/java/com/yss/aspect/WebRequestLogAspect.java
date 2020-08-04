package com.yss.aspect;

import com.alibaba.fastjson.JSONObject;
import com.yss.common.bean.ApiResult;
import com.yss.common.bean.OperatorLog;
import com.yss.common.bean.RequestHeaderContext;
import com.yss.common.bean.eum.ResultCode;
import com.yss.common.constant.BooleanClass;
import com.yss.common.constant.UrlConstant;
import com.yss.common.log.LogConfig;
import com.yss.redis.RedisFactory;
import com.yss.redis.RedisService;
import com.yss.util.AopParamsUtil;
import com.yss.util.WebToolUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

/**
 * @author shuoshi.yan
 * @package:com.yss.aspect
 * @className:WebRequestLogAspect
 * @description:日志请求切面拦截
 * @date 2018-02-07 10:18
 **/
@Aspect
@Component
@Slf4j
public class WebRequestLogAspect {

  @Autowired
  private RedisFactory redisFactory;
  @Autowired
  private LogConfig logConfig;
//    @Autowired
//    private ExternalAuthService externalAuthService;//插入日志接口


  @Pointcut("@annotation(com.yss.aspect.annotation.DelLog)")
  public void annotationPointcut() {
  }


  /**
   * @author:shuoshi.yan
   * @description:新增操作日志拦截
   * @date: 2020/08/04
   */
  @AfterReturning(pointcut = "execution(public * com.yss.*.controller..*.insert*(..))" +
      "||execution(public * com.yss.*.controller..*.add*(..))" +
      "||execution(public * com.yss.*.controller..*.save*(..))", returning = "result")
  public void doAddData(JoinPoint joinPoint, Object result) {
    log.info("新增拦截");
    log.error("doAddData" + logConfig.getOperateLog());
    if (BooleanClass.FALSE_STRING.equals(logConfig.getOperateLog())) {
      return;
    }
    if (BooleanClass.TRUE_STRING.equals(logConfig.getOperateLog())) {
      RedisService redisService = redisFactory.getRedis();
      try {
        long beginTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String beanName = joinPoint.getSignature().getDeclaringTypeName();
        OperatorLog optLog = new OperatorLog();
        String[] beans = beanName.split("\\.");
        if (beans.length > 0) {
          optLog.setClassName(beans[beans.length - 1]);
        }
        String uri = request.getRequestURI();
        String[] uris = uri.split("/");
        if (uris.length > 1) {
          optLog.setServiceName(uris[1]);
        }
        if (uris.length > 2) {
          optLog.setModuleName(uris[2]);
        }
        if (uris.length > 3) {
          optLog.setFunctionName(uris[3]);
        }
        String accesToken = RequestHeaderContext.getInstance().getAccesToken();

        if (StringUtils.isNotEmpty(accesToken)) {
          optLog.setUser(redisService.getUserId(accesToken));
        }
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
          s.append(JSONObject.toJSONString(joinPoint.getArgs()[i]));
        }
        String params = AopParamsUtil.getParams(request.getMethod(), joinPoint);
        optLog.setMethodName(joinPoint.getSignature().getName());
        optLog.setMethod(request.getMethod());
        optLog.setParamData(params != null ? params.toString() : "");
        optLog.setIp(WebToolUtils.ipToLong(WebToolUtils.getIpAddress(request)));
        optLog.setSessionId(request.getSession().getId());
        optLog.setUrl(uri);
        optLog.setRequestTime(new Timestamp(beginTime));
        JSONObject jsonObj = null;
        if ("/schoolbase/schoolbase/staffinfo/staff/editstaff".equals(uri)) {
          //教职工新增
          if (s.toString().contains("isDelete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else if (s.toString().contains("is_delete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else {
            optLog.setRemark(UrlConstant.EDIE_DATA);
            optLog.setType("2");
          }
        } else {
          jsonObj = JSONObject.parseObject(params);
          if (jsonObj.containsKey("isDelete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else if (jsonObj.containsKey("is_delete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else {
            optLog.setRemark(UrlConstant.EDIE_DATA);
            optLog.setType("2");
          }
        }
        //返回结果判断
        ApiResult apiResult = (ApiResult) result;
        if ((int) ResultCode.SUCCESS.getCode() == (int) apiResult.getCode()) {
//                    log.info("新增操作完成");
          // 处理完请求，返回内容
          if (optLog == null) {
            return;
          }
          try {
            optLog.setResult(JSONObject.toJSONString(result));
          } catch (Exception e) {
            optLog.setResult("新增异常，无正常返回结果");
          }
          long endTime = System.currentTimeMillis();
          optLog.setReturnTime(new Timestamp(endTime));
          optLog.setResponseTime((endTime - beginTime) / 1000);
          optLog.setRemark(UrlConstant.ADD_DATA);
          optLog.setType("1");
          log.info("新增操作日志参数++++" + JSONObject.toJSONString(optLog));
          //插入数据库
//          externalAuthService.newLog(optLog);
        } else {
          log.info("新增操作失败，不插入日志记录！");
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * @author:shuoshi.yan
   * @description:更新操作日志拦截
   * @date: 2018/9/17 15:36
   */
  @AfterReturning(pointcut = "execution(public * com.yss.*.controller..*.update*(..)) " +
      "|| execution(public * com.yss.*.controller..*.edit*(..))", returning = "result")
  public void doUpdateData(JoinPoint joinPoint, Object result) {
//        log.info("更新拦截");
    if (BooleanClass.FALSE_STRING.equals(logConfig.getOperateLog())) {
      return;
    }
//        log.info("进入更新拦截");
    if (BooleanClass.TRUE_STRING.equals(logConfig.getOperateLog())) {
      RedisService redisService = redisFactory.getRedis();
      try {
        long beginTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String beanName = joinPoint.getSignature().getDeclaringTypeName();
        String[] beans = beanName.split("\\.");
        OperatorLog optLog = new OperatorLog();
        if (beans.length > 0) {
          optLog.setClassName(beans[beans.length - 1]);
        }
        String uri = request.getRequestURI();
        if ("/schooldata/basedata/placemanage/editeplacel".equals(uri)) {
          //基础信息管理、学生基础信息管理编辑接口
          String[] uris = uri.split("/");
          if (uris.length > 1) {
            optLog.setServiceName(uris[1]);
          }
          optLog.setModuleName("base");
          optLog.setFunctionName("personinfo");
        } else if ("/schoolfront/schoolbase/schoolcarinformation/updateCar".equals(uri)) {
          //基础信息管理、车辆信息管理编辑接口
          String[] uris = uri.split("/");
          if (uris.length > 1) {
            optLog.setServiceName(uris[1]);
          }
          optLog.setModuleName("base");
          optLog.setFunctionName("schoolcarinfo");
        } else {
//                    log.info("uri:" + uri);
          String[] uris = uri.split("/");
          if (uris.length > 1) {
            optLog.setServiceName(uris[1]);
          }
          if (uris.length > 2) {
            optLog.setModuleName(uris[2]);
          }
          if (uris.length > 3) {
            optLog.setFunctionName(uris[3]);
          }
        }
        String accesToken = RequestHeaderContext.getInstance().getAccesToken();

        if (StringUtils.isNotEmpty(accesToken)) {
          optLog.setUser(redisService.getUserId(accesToken));
        }
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
          s.append(JSONObject.toJSONString(joinPoint.getArgs()[i]));
        }
//                log.info("s::::" + s);
        String params = AopParamsUtil.getParams(request.getMethod(), joinPoint);
        optLog.setMethodName(joinPoint.getSignature().getName());
        optLog.setMethod(request.getMethod());
        optLog.setParamData(params != null ? params.toString() : "");
        optLog.setIp(WebToolUtils.ipToLong(WebToolUtils.getIpAddress(request)));
        optLog.setSessionId(request.getSession().getId());
        optLog.setUrl(uri);
        //删除判断
        JSONObject jsonObj = null;
        if ("/schoolbase/schoolbase/staffinfo/staff/editstaff".equals(uri)) {
          //教职工修改
          if (s.toString().contains("isDelete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else if (s.toString().contains("is_delete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else {
            optLog.setRemark(UrlConstant.EDIE_DATA);
            optLog.setType("2");
          }
        } else {
          jsonObj = JSONObject.parseObject(params);
          if (jsonObj.containsKey("isDelete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else if (jsonObj.containsKey("is_delete")) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else {
            optLog.setRemark(UrlConstant.EDIE_DATA);
            optLog.setType("2");
          }
        }
        optLog.setRequestTime(new Timestamp(beginTime));
        ApiResult apiResult = (ApiResult) result;
        if ((int) ResultCode.SUCCESS.getCode() == (int) apiResult.getCode()) {
          // 处理完请求，返回内容
          if (optLog == null) {
            return;
          }
          try {
            optLog.setResult(JSONObject.toJSONString(result));
          } catch (Exception e) {
            optLog.setResult("更新异常，无正常返回结果");
          }
          long endTime = System.currentTimeMillis();
          optLog.setReturnTime(new Timestamp(endTime));
          optLog.setResponseTime((endTime - beginTime) / 1000);
          //插入数据库
//          externalAuthService.newLog(optLog);
        } else {
//                    log.info("更新操作失败，不插入日志记录！");
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * @author:shuoshi.yan
   * @description:删除操作日志拦截
   * @date: 2018/9/17 15:36
   */
//    @AfterReturning(pointcut = "annotationPointcut()", returning = "result")
  @AfterReturning(pointcut = "execution(public * com.yss.*.controller..*.delete*(..))", returning = "result")
  public void doDelData(JoinPoint joinPoint, Object result) {
//        log.info("删除日志拦截");
    if (BooleanClass.FALSE_STRING.equals(logConfig.getOperateLog())) {
      return;
    }
//        log.info("进入删除拦截");
    if (BooleanClass.TRUE_STRING.equals(logConfig.getOperateLog())) {
      RedisService redisService = redisFactory.getRedis();
      try {
        long beginTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String beanName = joinPoint.getSignature().getDeclaringTypeName();
        String[] beans = beanName.split("\\.");
        OperatorLog optLog = new OperatorLog();
        if (beans.length > 0) {
          optLog.setClassName(beans[beans.length - 1]);
        }
        String uri = request.getRequestURI();
        String[] uris = uri.split("/");
        if (uris.length > 1) {
          optLog.setServiceName(uris[1]);
        }
        if (uris.length > 2) {
          optLog.setModuleName(uris[2]);
        }
        if (uris.length > 3) {
          optLog.setFunctionName(uris[3]);
        }
        String accesToken = RequestHeaderContext.getInstance().getAccesToken();

        if (StringUtils.isNotEmpty(accesToken)) {
          optLog.setUser(redisService.getUserId(accesToken));
        }
        String params = AopParamsUtil.getDelParams(request.getMethod(), joinPoint);
        optLog.setMethodName(joinPoint.getSignature().getName());
        optLog.setMethod(request.getMethod());
        optLog.setParamData(params != null ? params.toString() : "");
        optLog.setIp(WebToolUtils.ipToLong(WebToolUtils.getIpAddress(request)));
        optLog.setSessionId(request.getSession().getId());
        optLog.setUrl(uri);
        //删除判断
        JSONObject jsonObj = JSONObject.parseObject(params);
        //权限管理批量删除接口判断
        if (jsonObj.containsKey("oprate")) {
          if ("3".equals(jsonObj.get("oprate").toString())) {
            optLog.setType("3");
            optLog.setRemark(UrlConstant.DEL_DATA);
          } else {
            return;
          }
        } else {
          optLog.setType("3");
          optLog.setRemark(UrlConstant.DEL_DATA);
        }
        optLog.setRequestTime(new Timestamp(beginTime));
        ApiResult apiResult = (ApiResult) result;
        if ((int) ResultCode.SUCCESS.getCode() == (int) apiResult.getCode()) {
          // 处理完请求，返回内容
          if (optLog == null) {
            return;
          }
          try {
            optLog.setResult(JSONObject.toJSONString(result));
          } catch (Exception e) {
            optLog.setResult("删除异常，无正常返回结果");
          }
          long endTime = System.currentTimeMillis();
          optLog.setReturnTime(new Timestamp(endTime));
          optLog.setResponseTime((endTime - beginTime) / 1000);
          //插入数据库
//          externalAuthService.newLog(optLog);
        } else {
          log.info("删除操作失败，不插入日志记录！");
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }


}
