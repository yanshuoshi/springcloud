package com.yss.externalcall;

import com.yss.common.bean.HttpResult;
import com.yss.externalcall.impl.ExternalDataServiceHystrix;
import com.yss.externalcall.pojo.QueryPlaceDeviceParams;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author shuoshi.yan
 * @className:
 * @description:字典项外部接口
 * @date
 **/
@FeignClient(value = "schooldata",fallback = ExternalDataServiceHystrix.class)
public interface ExternalDataService {

    /**
     * @author:shuoshi.yan
     * @description:
     */
    @RequestMapping(value = "/schooldata/mapgis/placedevice", method = RequestMethod.POST)
    HttpResult getPlaceDeviceData(@RequestBody QueryPlaceDeviceParams queryPlaceDeviceParams);

}
