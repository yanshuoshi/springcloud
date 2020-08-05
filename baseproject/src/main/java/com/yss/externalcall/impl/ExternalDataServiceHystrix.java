package com.yss.externalcall.impl;

import com.yss.common.bean.HttpResult;
import com.yss.common.constant.CommonConstant;
import com.yss.common.eum.ResultCode;
import com.yss.externalcall.ExternalDataService;
import com.yss.externalcall.pojo.QueryPlaceDeviceParams;
import org.springframework.stereotype.Component;


/**
 * @author shuoshi.yan
 * @description:
 * @date 2020/08/05
 **/
@Component
public class ExternalDataServiceHystrix implements ExternalDataService {

    @Override
    public HttpResult getPlaceDeviceData(QueryPlaceDeviceParams queryPlaceDeviceParams) {
        HttpResult httpResult = new HttpResult();
        httpResult.setCode(ResultCode.DEF_ERROR.getCode());
        httpResult.setMessage(CommonConstant.EXTERNALL_ERROR);
        return httpResult;
    }

}
