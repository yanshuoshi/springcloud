package com.yss.util;

import com.alibaba.fastjson.JSONObject;
import com.yss.common.constant.UrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author Shuoshi.yan
 * @description:aop拦截方法入参
 * @date 2020/08/04
 **/
@Slf4j
public class AopParamsUtil {

    /**
     * @author:shuoshi.yan
     * @description:获取参数
     */
    public static String getParams(String method, JoinPoint joinPoint) {
        String params = "";
        try {
            if (UrlConstant.POST.equals(method)) {
                params = argsArrayToString(joinPoint.getArgs());
            } else {
                params = getFieldsName(joinPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    /**
     * @author:shuoshi.yan
     * @description:获取参数
     */
    public static String getDelParams(String method, JoinPoint joinPoint) {
        String params = "";
        try {
//            if (UrlConstant.POST.equals(method)) {
//                params = argsArrayToString(joinPoint.getArgs());
//            } else {
                params = getFieldsName(joinPoint);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 请求参数拼装
     *
     * @param paramsArray
     * @return
     */
    private static String argsArrayToString(Object[] paramsArray) {
        StringBuffer stringBuffer = new StringBuffer();
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                stringBuffer.append(JSONObject.toJSONString(paramsArray[i]));
            }
        }
        return stringBuffer.toString();
    }


    private static String getFieldsName(JoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException {
        String classType = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        // 参数值
        Object[] args = joinPoint.getArgs();
        Class<?>[] classes = new Class[args.length];
        for (int k = 0; k < args.length; k++) {
            if (!args[k].getClass().isPrimitive()) {
                // 获取的是封装类型而不是基础类型
                String result = args[k].getClass().getName();
                Class s = map.get(result);
                classes[k] = s == null ? args[k].getClass() : s;
            }
        }
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        // 获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
        Method method = Class.forName(classType).getMethod(methodName, classes);
        // 参数名
        String[] parameterNames = pnd.getParameterNames(method);
        // 通过map封装参数和参数值
        HashMap<String, Object> paramMap = new HashMap();
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        return JSONObject.toJSONString(paramMap);
    }


    private static HashMap<String, Class> map = new HashMap<String, Class>() {
        {
            put("java.lang.Integer", int.class);
            put("java.lang.Double", double.class);
            put("java.lang.Float", float.class);
            put("java.lang.Long", long.class);
            put("java.lang.Short", short.class);
            put("java.lang.Boolean", boolean.class);
            put("java.lang.Char", char.class);
        }
    };
}
