package com.yss.user.pojo;

import com.yss.common.bean.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shuoshi.yan
 * @package:com.yss.user.pojo
 * @className:
 * @description:用户实体
 * @date 2018-02-08 16:28
 **/
@Data
public class UserPo extends BaseEntity implements Serializable {
    /*权限种类(账号种类)*/
    private Integer authType;
    /*场所类型*/
    private String authParams;
    /*用户名*/
    private String userName;
    /*密码*/
    private String passWord;
    /*姓名*/
    private String name;
    /*手机号*/
    private String phone;
    /*图片地址*/
    private String pic;
    /*组织机构或场所编码*/
    private String organization;
    /*账号类型*/
    private Integer type;
    /*账号状态*/
    private Integer state;
    /*角色编码*/
    private String roleCode;
    /*数据权限范围*/
    private String dataAuth;
    /*上级组织机构*/
    private String superOrg;
    /*有线mac地址*/
    private String wiredMac;
    /*无线mac地址*/
    private String wirelessMac;
}
