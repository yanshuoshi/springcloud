package com.yss.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author shuoshi.yan
 * @package:com.yss.user.mapper
 * @className:UserMapper
 * @description:用户登录
 * @date 2018-02-08 16:21
 **/
@Mapper
public interface UserMapper {

    /**
     * @author:shuoshi.yan
     * @description:用户查询
     * @date: 2018/2/8 16:49
     */
    Integer queryIsHavaUser(@Param("user_name")String userName);

}
