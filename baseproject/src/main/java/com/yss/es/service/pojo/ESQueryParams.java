package com.yss.es.service.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shuoshi.yan
 * @package:com.yss.es.service.pojo
 * @className:
 * @description:ES查询封装参数
 * @date 2020/08/05
 **/
@Data
public class ESQueryParams {

    /*索引*/
    private String index;
    /*类型*/
    private String type;
    /*完全匹配字段*/
    private Map<String, String> terms;
    /*完全不匹配字段*/
    private Map<String, List<String>> noTermsList;
    /*右模糊查询*/
    private Map<String, String> rightWildcards;
    /*完全模糊查询*/
    private Map<String, String> allWildcards;
    /*范围查询*/
    private Map<String, RangeEntity> ranges;
    /*一次匹配多个值*/
    private Map<String, List<String>> termsList;

    private List<String> existsFiles = new ArrayList<>();

    /*排序字段，降序*/
    private String sort;
    /*排序字段,升序*/
    private String sortASC;
    /*起始记录条数*/
    private Integer from;
    /*返回条数*/
    private Integer size;
    /**
     * @Field：es分页下一条数据
     */
    private Object[] next;
    /**
     * @Field：es分页上一条数据
     */
    private Object[] previous;
    /**
     * @Field：查询顺序
     */
    private String order;


    public ESQueryParams() {
    }

    public ESQueryParams(String index, String type, String sortDESC) {
        this.index = index;
        this.type = type;
        this.sort= sortDESC;
    }

    public ESQueryParams(String index, String type, String sortDESC, String sortASC) {
        this.index = index;
        this.type = type;
        this.sort= sortDESC;
        this.sortASC = sortASC;
    }
}
