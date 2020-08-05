package com.yss.common.bean;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Shuoshi.yan
 * @description:实体类公共字段
 * @date 2020/08/05
 **/
@Data
public class BaseEntity {
    /**id*/
    private Integer id;
    /**编码*/
    private String code;
    /**是否删除*/
    private Integer is_delete;
    /**新建时间*/
    private Timestamp create_time;
    /**新建人*/
    private String create_user;
    /**修改时间*/
    private Timestamp update_time;
    /**修改人*/
    private String update_user;
    /**备注*/
    private String remark;

}
