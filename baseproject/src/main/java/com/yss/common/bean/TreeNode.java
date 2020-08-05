package com.yss.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Shuoshi.yan
 * @description:树结构
 * @date 2020/08/05
 **/
@Data
public class TreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点编码
     */
    private String id;

    /**
     * 节点名称
     */
    private String text;

    /**
     * 节点编码
     */
    private String value;

    /**
     * 是否显示check复选框   显示：true  不显示：false
     */
    private Boolean showcheck; //给true

    private Boolean complete; //给true

    /**
     * 当前节点下包含子节点时为true ，否则(当前节点为叶子节点)为false
     */
    private Boolean isexpand; //第一级是true   剩下为false

    /**
     * 节点的复选框选中时为1，未选中为0，初始化时默认为0
     */
    private int checkstate;//都是0

    private int sum;

    private Boolean hasChildren;//是否有子节点

    private List<TreeNode> ChildNodes;//子节点
}
