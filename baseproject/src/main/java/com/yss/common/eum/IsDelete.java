package com.yss.common.eum;

/**
 * @author Shuoshi.yan
 * @description:是否删除枚举
 * @date 2020/08/05
 **/
public enum IsDelete {

    Y(1),N(0);

    private int code;

    private IsDelete(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
