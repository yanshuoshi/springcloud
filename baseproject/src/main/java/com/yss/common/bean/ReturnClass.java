package com.yss.common.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shuoshi.yan
 * @Description: ReturnClass
 * @date 2020/08/05
 */
@Data
public class ReturnClass<T> implements Serializable {

    /**
     * run success
     */
    private Boolean success;

    /**
     * error message
     */
    private String message;

    /**
     * return data
     */
    private T data;

    /**
     * return total
     */
    private Integer total;

    public void setSuccess() {
        this.success = true;
    }

    public ReturnClass() {
        this.success = false;
        this.message = "Abnormal server";
    }

}
