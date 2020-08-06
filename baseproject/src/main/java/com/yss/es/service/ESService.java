package com.yss.es.service;

import com.yss.es.service.pojo.ESReturn;
import com.yss.es.service.pojo.ESQueryParams;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author shuoshi.yan
 * @package:com.yss
 * @className:
 * @description:ES服务接口
 * @date 2020/08/05
 **/

public interface ESService {

    /**
     * @param index 索引
     * @param type  类型
     * @param obj   插入的对象
     * @author:shuoshi.yan
     * @description:新增接口
     */
    Boolean add(String index, String type, Object obj);

    /**
     * @param index           索引
     * @param type            类型
     * @param id
     * @param xContentBuilder 更新内容
     * @author:shuoshi.yan
     * @description:更新接口
     */
    Boolean update(String index, String type, String id, XContentBuilder xContentBuilder);

    /**
     * @param index        索引
     * @param type         类型
     * @param queryBuilder 拼接的查询条件
     * @param sort         排序字段，默认降序
     * @param from         起始记录位置
     * @param size         返回条数
     * @author:shuoshi.yan
     * @description:查询接口
     */
    ESReturn<List<Map<String, Object>>> query(String index, String type, QueryBuilder queryBuilder, String sort, Integer from, Integer size);

    /**
     * @author:shuoshi.yan
     * @description:es查询封装
     */
    ESReturn<List<Map<String, Object>>> queryES(ESQueryParams esQueryParams);


    /**
     * @param jsonDoc json串数据
     * @author:shuoshi.yan
     * @description:es查询封装
     */
    Boolean updateES(String index, String type, String id, String jsonDoc);


    /**
     * @param index        索引
     * @param type         类型
     * @param queryBuilder 拼接的查询条件
     * @param sortDESC         排序字段，降序
     * @param sortASC         排序字段，升序
     * @param from         起始记录位置
     * @param size         返回条数
     * @param next         查询当前页的sortvalue值
     * @param order        正查询还是反查询
     * @author:shuoshi.yan
     * @description:查询接口，判断升序还是降序
     */
    ESReturn<List<Map<String, Object>>> querySmart(String index, String type, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer from, Integer size, Object[] previous, Object[] next, String order);
    ESReturn<List<Map<String, Object>>> querySmartExport(String index, String type, QueryBuilder queryBuilder,String sortDESC, String sortASC, Integer from, Integer size,Object[] previous,Object[] next,String order);


    /**
     * @author:shuoshi.yan
     * @description:多index，多type查询
     * @param: elasticQueryParams
     * @return: SearchResponse
     */
    SearchResponse queryElastic(ESQueryParams esQueryParams)throws Exception;

}
