package com.yss.es.service.pojo;

import lombok.Data;

/**
 * @author Shuoshi.yan
 * @description:ES分页查询封装类
 * @date 2020/08/05
 **/
@Data
public class ESReturn<T> {

    /** Result data */
    private T data;

    /** Total item size */
    private long total;

    /** 
    * @Field：es分页上一条数据
    */ 
    private Object[] previous;

    /**
     * @Field：es分页下一条数据
     */
    private Object[] next;

    /** 
    * @Field：查询顺序
    */ 
    private String order;


}
