package com.yss.common.bean;/**
 * Created by start on 2018/3/9.
 */

import lombok.Data;

import java.io.Serializable;

/**
 * @author shuoshi.yan
 * @description:ZTree目录树结构
 * @date 2020/08/05
 **/
@Data
public class ZTreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点编码
     */
    private String id;

    /**
     * 父节点编码
     */
    private String pId;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 当前节点是否展开
     */
    private boolean open;

    /**
     * 是否是叶子节点
     */
    private boolean isChildNode;

    /**
     * 叶子节点-图标
     */
    private String icon;

    /**
     * 是否有复选框：true-没有；false-有
     */
    private boolean nocheck;

    /**
     * 是否是最后一级：true-是；false-否
     */
    private boolean isLastNode;

    /**
     * 是否选中：true-是；false-否
     */
    private boolean checked;

    /**
     * 是否可选：true-不让选；false-让选
     */
    private boolean chkDisabled;

}