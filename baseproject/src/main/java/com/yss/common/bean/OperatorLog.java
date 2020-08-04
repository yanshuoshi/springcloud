package com.yss.common.bean;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Shuoshi.yan
 * @description:日志实体类
 * @date 2020/08/04
 **/
@Data
public class OperatorLog {
    private Integer id;
    private Long ip;
    /*登录名*/
    private String user;
    private String roleCode;
    private String role;
    private String url;

    private String method;
    /*服务名*/
    private String serviceName;
    /*模块名*/
    private String moduleName;
    /*功能名*/
    private String functionName;
    /*类名*/
    private String className;
    /*方法名*/
    private String methodName;
    private String paramData;
    private String sessionId;
    private Timestamp requestTime;
    private Timestamp returnTime;
    private long responseTime;
    private String result;
    private String remark;

    private String type;

}
