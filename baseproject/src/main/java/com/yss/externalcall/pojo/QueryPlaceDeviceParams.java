package com.yss.externalcall.pojo;

import com.yss.common.bean.PageEntity;
import lombok.Data;

/**
 * @author shuoshi.yan
 * @description:
 * @date 2020/08/05
 **/
@Data
public class QueryPlaceDeviceParams extends PageEntity {

    /**
     * 搜索名称
     */
    private String queryName;
    /**
     * 类型
     */
    private String type;
    /**
     * 矩形起点x
     */
    private Double x1;
    /**
     * 矩形起点y
     */
    private Double y1;
    /**
     * 矩形终点x
     */
    private Double x2;
    /**
     * 矩形终点y
     */
    private Double y2;

}