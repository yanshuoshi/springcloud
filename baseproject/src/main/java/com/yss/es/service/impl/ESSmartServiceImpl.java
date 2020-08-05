package com.yss.es.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yss.es.service.ESService;
import com.yss.es.service.constant.ESQueryConstant;
import com.yss.es.service.pojo.ESQueryParams;
import com.yss.es.service.pojo.ESReturn;
import com.yss.es.service.pojo.RangeEntity;
import com.yss.util.ClientHelper;
import com.yss.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @description:es深度查询实现
 * @author:Shuoshi.yan
 * @date:2020/08/05
 **/
@Service
@Slf4j
public class ESSmartServiceImpl implements ESService {

    @Autowired
    private ClientHelper clientHelper;

    /**
     * @param index 索引
     * @param type  类型
     * @param obj   插入的对象
     * @author:shuoshi.yan
     * @description:新增接口
     * @date: 2018/3/29 10:20
     */
    @Override
    public Boolean add(String index, String type, Object obj) {
        String jsonObject = JSONObject.toJSONString(obj);
//        JsonObject jsonObject = objToJson(obj);
        Client client = clientHelper.getClient();
        try {
            IndexResponse response = client.prepareIndex(index, type).setSource(jsonObject, XContentType.JSON).get();
            RestStatus status = response.status();
            int status1 = status.getStatus();
            log.info(">>>ES存储结果" + status1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param index           索引
     * @param type            类型
     * @param id
     * @param xContentBuilder 更新内容
     * @author:shuoshi.yan
     * @description:更新接口
     * @date: 2018/4/9 12:05
     */
    @Override
    public Boolean update(String index, String type, String id, XContentBuilder xContentBuilder) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index);
        updateRequest.type(type);
        updateRequest.id(id);
        updateRequest.doc(xContentBuilder);
        Client client = clientHelper.getClient();
        try {
            UpdateResponse updateResponse = client.update(updateRequest).get();
            int status = updateResponse.status().getStatus();
            log.info(">>>ES更新结果：" + status);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param index        索引
     * @param type         类型
     * @param queryBuilder 拼接的查询条件
     * @param sort         排序字段，默认降序
     * @param from         起始记录位置
     * @param size         返回条数
     * @author:shuoshi.yan
     * @description:查询接口
     * @date: 2018/3/29 10:33
     */
    @Override
    public ESReturn<List<Map<String, Object>>> query(String index, String type, QueryBuilder queryBuilder, String sort, Integer from, Integer size) {
        ESReturn<List<Map<String, Object>>> esReturn = new ESReturn<List<Map<String, Object>>>();
        Client client = clientHelper.getClient();
        SearchRequestBuilder srb = client.prepareSearch(index).setTypes(type);
        SearchResponse sr = srb.setQuery(queryBuilder).setFrom(from).setSize(size).addSort(SortBuilders.fieldSort(sort).unmappedType("integer").order(SortOrder.DESC)).addSort(SortBuilders.fieldSort("_uid").unmappedType("integer").order(SortOrder.DESC)).execute().actionGet(); // 分页排序所有

        // TODO : sr.getHits().totalHits();
        long totalHits = sr.getHits().getTotalHits().value;
        esReturn.setTotal(totalHits);
        SearchHits hits = sr.getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            //TODO (Map<String, Object>) hit.getSource();
            Map<String, Object> map = hit.getSourceAsMap();
            result.add(map);
        }
        esReturn.setData(result);
        return esReturn;
    }

    /**
     * @param esQueryParams
     * @author:shuoshi.yan
     * @description:es查询封装
     * @date: 2018/5/22 18:22
     */
    @Override
    public ESReturn<List<Map<String, Object>>> queryES(ESQueryParams esQueryParams) {
        ESReturn<List<Map<String, Object>>> esReturn = new ESReturn<>();
        if (StringUtils.isEmpty(esQueryParams.getIndex())) {
            log.info(ESQueryConstant.INDEX_NULL);
            return esReturn;
        }
        if (StringUtils.isEmpty(esQueryParams.getType())) {
            log.info(ESQueryConstant.TYPE_NULL);
            return esReturn;
        }
        if (StringUtils.isEmpty(esQueryParams.getSort())) {
            log.info(ESQueryConstant.SORT_NULL);
            return esReturn;
        }
        if (esQueryParams.getFrom() == null) {
            esQueryParams.setFrom(0);
        }
        if (esQueryParams.getSize() == null) {
            esQueryParams.setSize(10);
        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //完全不匹配查询
        if (CommonUtil.isNotMapNull(esQueryParams.getNoTermsList())) {
            for (String key : esQueryParams.getNoTermsList().keySet()) {
                TermsQueryBuilder notTermsQueryBuilder = QueryBuilders.termsQuery(key, esQueryParams.getNoTermsList().get(key));
                queryBuilder.mustNot(notTermsQueryBuilder);
            }
        }
        //完全匹配查询
        if (CommonUtil.isNotMapNull(esQueryParams.getTerms())) {
            for (String key : esQueryParams.getTerms().keySet()) {
                TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(key, esQueryParams.getTerms().get(key));
                queryBuilder.must(termsQueryBuilder);
            }
        }
        //完全匹配查询
        if (CommonUtil.isNotMapNull(esQueryParams.getTermsList())) {
            for (String key : esQueryParams.getTermsList().keySet()) {
                TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(key, esQueryParams.getTermsList().get(key));
                queryBuilder.must(termsQueryBuilder);
            }
        }
        //完全模糊查询
        if (CommonUtil.isNotMapNull(esQueryParams.getAllWildcards())) {
            for (String key : esQueryParams.getAllWildcards().keySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key, "*" + esQueryParams.getAllWildcards().get(key) + "*");
                queryBuilder.must(wildcardQueryBuilder);
            }
        }

        //右模糊查询
        if (CommonUtil.isNotMapNull(esQueryParams.getRightWildcards())) {
            for (String key : esQueryParams.getRightWildcards().keySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key, esQueryParams.getRightWildcards().get(key) + "*");
                queryBuilder.must(wildcardQueryBuilder);
            }
        }
        //范围查询
        if (CommonUtil.isNotMapNull(esQueryParams.getRanges())) {
            for (String key : esQueryParams.getRanges().keySet()) {
                RangeEntity rangeEntity = (RangeEntity) esQueryParams.getRanges().get(key);
                if (StringUtils.isNotEmpty(rangeEntity.getFrom()) && StringUtils.isNotEmpty(rangeEntity.getTo())) {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).from(rangeEntity.getFrom(), true).to(rangeEntity.getTo(), true);// 包含上届
                    queryBuilder.must(rangeQueryBuilder);
                }

                if (StringUtils.isNotEmpty(rangeEntity.getFrom()) && StringUtils.isEmpty(rangeEntity.getTo())) {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).from(rangeEntity.getFrom(), true);// 包含上届
                    queryBuilder.must(rangeQueryBuilder);
                }
            }
        }
        //判断字段非空
        if (CommonUtil.isNotListNull(esQueryParams.getExistsFiles())) {
            for (String name : esQueryParams.getExistsFiles()) {
                queryBuilder.must(QueryBuilders.existsQuery(name));
            }
        }
        esReturn = querySmart(esQueryParams.getIndex(), esQueryParams.getType(), queryBuilder, esQueryParams.getSort(), esQueryParams.getSortASC(), esQueryParams.getFrom(), esQueryParams.getSize(), esQueryParams.getPrevious(), esQueryParams.getNext(), esQueryParams.getOrder());
        return esReturn;
    }

    /**
     * @param index
     * @param type
     * @param id
     * @param jsonDoc json串数据  @author:shuoshi.yan
     * @description:es查询封装
     * @date: 2018/5/22 18:22
     */
    @Override
    public Boolean updateES(String index, String type, String id, String jsonDoc) {
        Client client = clientHelper.getClient();
        UpdateResponse updateResponse = client.prepareUpdate(index, type, id).setDoc(jsonDoc, XContentType.JSON).get();
        RestStatus status = updateResponse.status();
        if (status.name().equals(ESQueryConstant.OK_STRING)) {
            log.info(">>>ES更新状态" + status.name());
            return true;
        }
        return false;
    }

    /**
     * @param esQueryParams
     * @author:shuoshi.yan
     * @description:多index，多type查询
     * @date: 2019/1/5 10:20
     * @param: elasticQueryParams
     * @return: SearchResponse
     */
    @Override
    public SearchResponse queryElastic(ESQueryParams esQueryParams) throws Exception {
        if (StringUtils.isEmpty(esQueryParams.getIndex())) {
            log.error(ESQueryConstant.INDEX_NULL);
            throw new Exception("index can not be null !");
        }
        if (StringUtils.isEmpty(esQueryParams.getType())) {
            log.error(ESQueryConstant.TYPE_NULL);
            throw new Exception("type can not be null !");
        }
        if (StringUtils.isEmpty(esQueryParams.getSort())) {
            log.error(ESQueryConstant.SORT_NULL);
            throw new Exception("sort can not be null !");
        }
        if (esQueryParams.getFrom() == null) {
            esQueryParams.setFrom(0);
        } else if (esQueryParams.getFrom() < 0) {
            esQueryParams.setFrom(0);
        }
        if (esQueryParams.getSize() == null) {
            esQueryParams.setSize(10);
        }
        BoolQueryBuilder queryBuilder = getQueryBuilder(esQueryParams);
        Client client = clientHelper.getClient();
        SearchRequestBuilder srb = client.prepareSearch(esQueryParams.getIndex().split(",")).setTypes(esQueryParams.getType().split(",")).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchResponse searchResponse = srb.setQuery(queryBuilder).setFrom(esQueryParams.getFrom()).setSize(esQueryParams.getSize()).addSort(SortBuilders.fieldSort(esQueryParams.getSort()).unmappedType("integer").order(SortOrder.DESC)).execute().actionGet();
        return searchResponse;
    }

    /**
     * @param index        索引
     * @param type         类型
     * @param queryBuilder 拼接的查询条件
     * @param sortDESC     排序字段，降序
     * @param sortASC      排序字段，升序
     * @param from         起始记录位置
     * @param size         返回条数
     * @param next         查询当前页的sortvalue值
     * @param order        正查询还是反查询
     * @author:shuoshi.yan
     * @description:查询接口，判断升序还是降序
     * @date: 2018/3/29 10:33
     */
    @Override
    public ESReturn<List<Map<String, Object>>> querySmart(String index, String type, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer from, Integer size, Object[] previous, Object[] next, String order) {
        log.info("next" + next);
        log.info("previous" + previous);
        ESReturn<List<Map<String, Object>>> esReturn = new ESReturn<List<Map<String, Object>>>();
        Client client = clientHelper.getClient();
        SearchRequestBuilder srb = client.prepareSearch(index).setTypes(type);
        SearchRequestBuilder srbPr = client.prepareSearch(index).setTypes(type);
        //<1000降序查询
        if (from < 10000) {
            //下一页操作
            esReturn = queryDESC(srb, queryBuilder, sortDESC, sortASC, from, size, esReturn);
        } else {
            //容错
            if (from < 10030) {
                esReturn = queryDESC(srb, queryBuilder, sortDESC, sortASC, from, size, esReturn);
                esReturn.setPrevious(null);
                return esReturn;
            }
            SearchResponse sr = srb.setQuery(queryBuilder).execute().actionGet(); // 分页排序所有
            long totalHits = sr.getHits().getTotalHits().value;
            int num = (int) totalHits - from;
            log.info("num" + num);
            //尾页数判断
            if (num < 10000) {
                log.info(">>>尾页1000页查询");
                int fromSize = 0;
                if (num - 9 > 0) {
                    fromSize = num - 9;
                }
                esReturn = queryASC(srb, queryBuilder, sortDESC, sortASC, fromSize, size, esReturn);
            } else {
                //容错
                if (num < 10030) {
                    int fromSize = 0;
                    if (num - 9 > 0) {
                        fromSize = num - 9;
                    }
                    esReturn = queryASC(srb, queryBuilder, sortDESC, sortASC, fromSize, size, esReturn);
                    esReturn.setNext(null);
                    return esReturn;
                }
                //search_after查询
                log.info(">>>search_after查询");
                //正序查询
                if (ESQueryConstant.ORDER_ASC_STRING.equals(order)) {
                    log.info(">>>正序查询");
                    esReturn = queryASCSearchAfter(srb, queryBuilder, sortDESC, sortASC, size, previous, esReturn);
                }
                //倒序查询
                if (ESQueryConstant.ORDER_DESC_STRING.equals(order)) {
                    log.info(">>>倒序查询");
                    esReturn = queryDESCSearchAfter(srb, queryBuilder, sortDESC, sortASC, size, next, esReturn);
                }
            }

        }
        return esReturn;

    }

    @Override
    public ESReturn<List<Map<String, Object>>> querySmartExport(String index, String type, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer from, Integer size, Object[] previous, Object[] next, String order) {
        log.info("next:" + next);
        log.info("previous:" + previous);
        ESReturn<List<Map<String, Object>>> esReturn = new ESReturn<List<Map<String, Object>>>();
        Client client = clientHelper.getClient();
        SearchRequestBuilder srb = client.prepareSearch(index).setTypes(type);
        SearchRequestBuilder srbPr = client.prepareSearch(index).setTypes(type);
        //<1000降序查询
        if (from < 10000) {
            //下一页操作
            esReturn = queryDESC(srb, queryBuilder, sortDESC, sortASC, from, size, esReturn);
        } else {
            //容错
            //if (from < 10030) {
            //    esReturn = queryDESC(srb, queryBuilder, sortDESC, sortASC, from, size, esReturn);
            //    esReturn.setPrevious(null);
            //    return esReturn;
            //}
            SearchResponse sr = srb.setQuery(queryBuilder).execute().actionGet(); // 分页排序所有
            long totalHits = sr.getHits().getTotalHits().value;
            int num = (int) totalHits - from;
            log.info("num:" + num);
            //尾页数判断
            //if (num < 10000) {
            //    log.info(">>>尾页1000页查询");
            //    int fromSize = 0;
            //    if (num - 9 > 0) {
            //        fromSize = num - 9;
            //    }
            //    esReturn = queryASC(srb, queryBuilder, sortDESC, sortASC, fromSize, size, esReturn);
            //} else {
                //容错
                //if (num < 10030) {
                //    int fromSize = 0;
                //    if (num - 9 > 0) {
                //        fromSize = num - 9;
                //    }
                //    esReturn = queryASC(srb, queryBuilder, sortDESC, sortASC, fromSize, size, esReturn);
                //    esReturn.setNext(null);
                //    return esReturn;
                //}
                //search_after查询
                log.info(">>>search_after查询");
                //正序查询
                if (ESQueryConstant.ORDER_ASC_STRING.equals(order)) {
                    log.info(">>>正序查询");
                    esReturn = queryASCSearchAfter(srb, queryBuilder, sortDESC, sortASC, size, previous, esReturn);
                }
                //倒序查询
                if (ESQueryConstant.ORDER_DESC_STRING.equals(order)) {
                    log.info(">>>倒序查询");
                    esReturn = queryDESCSearchAfter(srb, queryBuilder, sortDESC, sortASC, size, next, esReturn);
                }
            //}

        }
        return esReturn;

    }


    /**
     * @author:shuoshi.yan
     * @description:Object 转json
     * @date: 2020/08/05
     */
//    private JsonObject objToJson(Object obj) {
//        Field[] fields = obj.getClass().getDeclaredFields();
//        JsonObject jsonObject = new JsonObject();
//        for (int i = 0, len = fields.length; i < len; i++) {
//            // 对于每个属性，获取属性名
//            String varName = fields[i].getName();
//            try {
//                // 获取原来的访问控制权限
//                boolean accessFlag = fields[i].isAccessible();
//                // 修改访问控制权限
//                fields[i].setAccessible(true);
//                // 获取在对象f中属性fields[i]对应的对象中的变量
//                Object o;
//                try {
//                    o = fields[i].get(obj);
//                    if (o != null) {
//                        jsonObject.addProperty(varName, o.toString());
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//                // 恢复访问控制权限
//                fields[i].setAccessible(accessFlag);
//            } catch (IllegalArgumentException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return jsonObject;
//    }


    /**
     * @param searchRequestBuilder es查询类
     * @param esReturn             返回类型
     * @param queryBuilder         拼接的查询条件
     * @param sortDESC             排序字段，降序
     * @param sortASC              排序字段，升序
     * @param from                 起始记录位置
     * @param size                 返回条数
     * @author:shuoshi.yan
     * @description:查询接口，判断升序还是降序
     * @date: 2020/08/05
     * @return: ESReturn
     */
    private ESReturn queryDESC(SearchRequestBuilder searchRequestBuilder, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer from, Integer size, ESReturn esReturn) {
        log.info(">>>正常查询前1000页");
        esReturn.setOrder(ESQueryConstant.ORDER_DESC_STRING);
        SearchRequestBuilder searchRequestBuilder1 = searchRequestBuilder.setQuery(queryBuilder).setFrom(from).setSize(size).addSort(SortBuilders.fieldSort(sortDESC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC));
        if (StringUtils.isNotEmpty(sortASC)) {
            searchRequestBuilder1.addSort(SortBuilders.fieldSort(sortASC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.ASC));
        }
        SearchResponse sr = searchRequestBuilder1.addSort(SortBuilders.fieldSort(ESQueryConstant.UID_STRING).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC)).execute().actionGet(); // 分页排序所有
        long totalHits = sr.getHits().getTotalHits().value;
        esReturn.setTotal(totalHits);
        SearchHits hits = sr.getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            esReturn.setNext(hit.getSortValues());
            esReturn.setPrevious(hit.getSortValues());
            Map<String, Object> map = hit.getSourceAsMap();
            map.put(ESQueryConstant.ES_ID_STRING, hit.getId());
            map.put(ESQueryConstant.ID_STRING, hit.getId());
            result.add(map);
        }
        esReturn.setData(result);
        return esReturn;
    }


    /**
     * @param searchRequestBuilder es查询类
     * @param esReturn             返回类型
     * @param queryBuilder         拼接的查询条件
     * @param sortDESC             排序字段，降序
     * @param sortASC              排序字段，升序
     * @param from                 起始记录位置
     * @param size                 返回条数
     * @author:shuoshi.yan
     * @description:查询接口，判断升序还是降序
     * @date: 2018/3/29 10:33
     * @return: ESReturn
     */
    private ESReturn queryASC(SearchRequestBuilder searchRequestBuilder, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer from, Integer size, ESReturn esReturn) {
        esReturn.setOrder(ESQueryConstant.ORDER_ASC_STRING);
        //自定义排序
        SearchRequestBuilder searchRequestBuilder1 = searchRequestBuilder.setQuery(queryBuilder).setFrom(from).setSize(size).addSort(SortBuilders.fieldSort(sortDESC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.ASC));
        if (StringUtils.isNotEmpty(sortASC)) {
            searchRequestBuilder1.addSort(SortBuilders.fieldSort(sortASC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC));
        }
        SearchResponse sr = searchRequestBuilder1.addSort(SortBuilders.fieldSort(ESQueryConstant.UID_STRING).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC)).execute().actionGet();// 分页排序所有
        long totalHits = sr.getHits().getTotalHits().value;
        esReturn.setTotal(totalHits);

        SearchHits hits = sr.getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            esReturn.setPrevious(hit.getSortValues());
            esReturn.setNext(hit.getSortValues());
            Map<String, Object> map = (Map<String, Object>) hit.getSourceAsMap();
            map.put(ESQueryConstant.ES_ID_STRING, hit.getId());
            map.put(ESQueryConstant.ID_STRING, hit.getId());
            result.add(map);
        }
        Collections.sort(result, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String s1 = o1.get(sortDESC).toString();
                String s2 = o2.get(sortDESC).toString();
                return s1.compareTo(s2) * (false ? 1 : -1);
            }
        });
        //尾页显示条数处理
        int rem = 0;
        if (from == 0) {
            rem = (int) totalHits % size;
        }
        if (rem == 0) {
            esReturn.setData(result);
        } else {
            esReturn.setData(result.subList(0, rem));
        }
        return esReturn;
    }


    /**
     * @param searchRequestBuilder es查询类
     * @param esReturn             返回类型
     * @param queryBuilder         拼接的查询条件
     * @param sortDESC             排序字段，降序
     * @param sortASC              排序字段，升序
     * @param size                 返回条数
     * @param next                 查询当前页的sortvalue值
     * @author:shuoshi.yan
     * @description:查询接口，判断升序还是降序
     * @date: 2018/3/29 10:33
     * @return: ESReturn
     */
    private ESReturn queryDESCSearchAfter(SearchRequestBuilder searchRequestBuilder, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer size, Object[] next, ESReturn esReturn) {
        log.info(">>>queryDESCSearchAfter  从前向后翻页");
        esReturn.setOrder(ESQueryConstant.ORDER_DESC_STRING);
        SearchRequestBuilder searchRequestBuilder1 = searchRequestBuilder.setQuery(queryBuilder).searchAfter(next).setFrom(0).setSize(size).addSort(SortBuilders.fieldSort(sortDESC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC));// 分页排序所有
        if (StringUtils.isNotEmpty(sortASC)) {
            searchRequestBuilder1.addSort(SortBuilders.fieldSort(sortASC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.ASC));
        }
        SearchResponse sr = searchRequestBuilder1.addSort(SortBuilders.fieldSort(ESQueryConstant.UID_STRING).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC)).execute().actionGet();
        long totalHits = sr.getHits().getTotalHits().value;
        esReturn.setTotal(totalHits);
        SearchHits hits = sr.getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            esReturn.setNext(hit.getSortValues());
            esReturn.setPrevious(null);
            Map<String, Object> map = (Map<String, Object>) hit.getSourceAsMap();
            map.put(ESQueryConstant.ES_ID_STRING, hit.getId());
            map.put(ESQueryConstant.ID_STRING, hit.getId());
            result.add(map);
        }
        esReturn.setData(result);
        return esReturn;
    }


    /**
     * @param searchRequestBuilder es查询类
     * @param esReturn             返回类型
     * @param queryBuilder         拼接的查询条件
     * @param sortDESC             排序字段，降序
     * @param sortASC              排序字段，升序
     * @param size                 返回条数
     * @description:查询接口，判断升序还是降序
     * @date: 2018/3/29 10:33
     * @return: ESReturn
     */
    private ESReturn queryASCSearchAfter(SearchRequestBuilder searchRequestBuilder, QueryBuilder queryBuilder, String sortDESC, String sortASC, Integer size, Object[] previous, ESReturn esReturn) {
        log.info(">>>queryASCSearchAfter   从后向前翻页");
        esReturn.setOrder(ESQueryConstant.ORDER_ASC_STRING);
        //自定义排序
        SearchRequestBuilder searchRequestBuilder1 = searchRequestBuilder.setQuery(queryBuilder).searchAfter(previous).setFrom(0).setSize(size).addSort(SortBuilders.fieldSort(sortDESC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.ASC));// 分页排序所有
        if (StringUtils.isNotEmpty(sortASC)) {
            searchRequestBuilder1.addSort(SortBuilders.fieldSort(sortASC).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC));
        }
        SearchResponse sr = searchRequestBuilder1.addSort(SortBuilders.fieldSort(ESQueryConstant.UID_STRING).unmappedType(ESQueryConstant.INTEGER_STRING).order(SortOrder.DESC)).execute().actionGet();
        long totalHits = sr.getHits().getTotalHits().value;
        esReturn.setTotal(totalHits);
        SearchHits hits = sr.getHits();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            esReturn.setPrevious(hit.getSortValues());
            esReturn.setNext(null);
            Map<String, Object> map = (Map<String, Object>) hit.getSourceAsMap();
            map.put(ESQueryConstant.ES_ID_STRING, hit.getId());
            map.put(ESQueryConstant.ID_STRING, hit.getId());
            result.add(map);
        }
        Collections.sort(result, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String s1 = o1.get(sortDESC).toString();
                String s2 = o2.get(sortDESC).toString();
                return s1.compareTo(s2) * (false ? 1 : -1);
            }
        });
        esReturn.setData(result);
        return esReturn;
    }

    private BoolQueryBuilder getQueryBuilder(ESQueryParams esQueryParams) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //完全不匹配查询
        if (CommonUtil.isNotMapNull(esQueryParams.getNoTermsList())) {
            for (String key : esQueryParams.getNoTermsList().keySet()) {
                TermsQueryBuilder notTermsQueryBuilder = QueryBuilders.termsQuery(key, esQueryParams.getNoTermsList().get(key));
                queryBuilder.mustNot(notTermsQueryBuilder);
            }
        }
        //完全匹配查询
        if (CommonUtil.isNotMapNull(esQueryParams.getTerms())) {
            for (String key : esQueryParams.getTerms().keySet()) {
                TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(key, esQueryParams.getTerms().get(key));
                queryBuilder.must(termsQueryBuilder);
            }
        }
        //完全匹配查询
        if (CommonUtil.isNotMapNull(esQueryParams.getTermsList())) {
            for (String key : esQueryParams.getTermsList().keySet()) {
                TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(key, esQueryParams.getTermsList().get(key));
                queryBuilder.must(termsQueryBuilder);
            }
        }
        //完全模糊查询
        if (CommonUtil.isNotMapNull(esQueryParams.getAllWildcards())) {
            for (String key : esQueryParams.getAllWildcards().keySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key, "*" + esQueryParams.getAllWildcards().get(key) + "*");
                queryBuilder.must(wildcardQueryBuilder);
            }
        }

        //右模糊查询
        if (CommonUtil.isNotMapNull(esQueryParams.getRightWildcards())) {
            for (String key : esQueryParams.getRightWildcards().keySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key, esQueryParams.getRightWildcards().get(key) + "*");
                queryBuilder.must(wildcardQueryBuilder);
            }
        }
        //范围查询
        if (CommonUtil.isNotMapNull(esQueryParams.getRanges())) {
            for (String key : esQueryParams.getRanges().keySet()) {
                RangeEntity rangeEntity = (RangeEntity) esQueryParams.getRanges().get(key);
                if (StringUtils.isNotEmpty(rangeEntity.getFrom()) && StringUtils.isNotEmpty(rangeEntity.getTo())) {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).from(rangeEntity.getFrom(), true).to(rangeEntity.getTo(), true);// 包含上届
                    queryBuilder.must(rangeQueryBuilder);
                }

                if (StringUtils.isNotEmpty(rangeEntity.getFrom()) && StringUtils.isEmpty(rangeEntity.getTo())) {
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key).from(rangeEntity.getFrom(), true);// 包含上届
                    queryBuilder.must(rangeQueryBuilder);
                }
            }
        }
        //判断字段非空
        if (CommonUtil.isNotListNull(esQueryParams.getExistsFiles())) {
            for (String name : esQueryParams.getExistsFiles()) {
                queryBuilder.must(QueryBuilders.existsQuery(name));
            }
        }

        //匹配出空字段和null字段
//        if (CommonUtil.isNotListNull(esQueryParams.get())) {
//            for (String name : esQueryParams.getNullFiles()) {
//                queryBuilder.mustNot(QueryBuilders.regexpQuery(name, ESConstant.REGEXP_STRING));
//            }
//        }
        return queryBuilder;
    }
}
