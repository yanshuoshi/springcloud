package com.yss.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author shuoshi.yan
 * @description:删除日志拦截
 * @date 2020/08/04
 **/
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DelLog {
    String value() default "";
}
