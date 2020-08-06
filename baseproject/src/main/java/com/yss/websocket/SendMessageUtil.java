package com.yss.websocket;

import com.alibaba.fastjson.JSONObject;
import com.yss.websocket.pojo.SendMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Shuoshi.yan
 * @description:websocket推送工具类
 * @date 2020/08/06
 **/
@Component
public class SendMessageUtil {
  private static final String message = "school";
  private static final String EXCHANGE = "exchange_fanout";
  @Autowired
  private AmqpTemplate rabbitTemplate;

  /**
   * @param type        消息类型 1:职工  2：学校
   * @param toUserToken 要推送的用户token，如果不传则推送给全部订阅该消息全部用户
   * @param data        消息体
   * @author:shuoshi.yan
   * @description:推送方法
   * @date: 2020/08/06
   */
  public void send(String type, String toUserToken, String data) {
    System.out.println("type:" + type);
    System.out.println(toUserToken);
    System.out.println(data);
    SendMessage sendMessage = new SendMessage(type, toUserToken, null, data);
    this.rabbitTemplate.convertAndSend(message, JSONObject.toJSONString(sendMessage));
  }

  public void sendPS(String type, String toUserToken, String data) {
    System.out.println("type:" + type);
    System.out.println(toUserToken);
    System.out.println(data);
    SendMessage sendMessage = new SendMessage(type, toUserToken, null, data);
    this.rabbitTemplate.convertAndSend(EXCHANGE, "", JSONObject.toJSONString(sendMessage));
  }

}
