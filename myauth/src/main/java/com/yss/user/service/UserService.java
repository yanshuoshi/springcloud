package com.yss.user.service;

import com.yss.common.bean.ReturnClass;
import java.util.Map;

/**
 * @author shuoshi.yan
 * @description:用户服务层
 * @date 2020/08/06
 **/
public interface UserService {
    /**
     * @author:shuoshi.yan
     * @description:用户查询
     * @date: 2018/2/8 16:49
     */
    ReturnClass<Map<String, Object>> queryUser(String userName, String passWord, String auth,String mac);

}
