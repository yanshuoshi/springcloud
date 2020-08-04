package com.yss.common.log;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author shuoshi.yan
 * @description:操作日志开关配置
 * @date 2020/08/04
 **/
@Component
@RefreshScope
@Data
public class LogConfig {
    /*WebRequestLogAspect 日志开关*/
    @Value("${aspect.joinpoint.before:false}")
    private String before;
    @Value("${aspect.joinpoint.after:false}")
    private String after;

    /*WebRequestLogAspect 操作日志开关*/
    @Value("${aspect.joinpoint.operateLog:true}")
    private String operateLog;

    /*RequestParamLogAspect 日志开关*/
    @Value("${aspect.joinpoint.paramLog:false}")
    private String paramLog;

    /*ImgLogAspect 日志开关和配置*/
    @Value("${aspect.joinpoint.imgLog:false}")
    private String imgLog;
    @Value("${file.save.path}")
    private String savePath;


}
