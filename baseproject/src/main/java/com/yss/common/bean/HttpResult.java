package com.yss.common.bean;

/**
 * @author Shuoshi.yan
 * @description:HttpResult
 * @date 2020/08/05
 **/

public class HttpResult {
    /** Result status */
    private Integer code;
    /** Result message */
    private String message;
    /** Result data */
    private Object data;
    /** Total item size */
    private Integer total;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public HttpResult() {
    }


}
