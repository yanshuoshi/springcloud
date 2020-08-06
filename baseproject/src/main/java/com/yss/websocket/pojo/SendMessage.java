package com.yss.websocket.pojo;

import lombok.Data;

/**
 * @author Shuoshi.yan
 * @description:消息推送实体类
 * @date 2020/08/06
 **/
@Data
public class SendMessage {
    /*推送的消息类型*/
    private String type;

    /*用户登录生成的token*/
    private String toUserToken;

    private String fromUserToken;

    /*消息内容*/
    private String data;

    public SendMessage(String type, String toUserToken, String fromUserToken, String data) {
        this.type = type;
        this.toUserToken = toUserToken;
        this.fromUserToken = fromUserToken;
        this.data = data;
    }
}
