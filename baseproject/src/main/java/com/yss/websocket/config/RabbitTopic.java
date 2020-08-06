package com.yss.websocket.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Shuoshi.yan
 * @description:
 * @date 2020/08/06
 **/
@Configuration
public class RabbitTopic {

    final static String message = "closedPlace";
    final static String messages = "topic.messages";

    //创建队列
    @Bean
    public Queue queueMessage() {
        return new Queue(RabbitTopic.message);
    }

    //创建队列
    @Bean
    public Queue queueMessages() {
        return new Queue(RabbitTopic.messages);
    }

    //创建交换器
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("topicExchange");
    }

    //对列绑定并关联到ROUTINGKEY
    @Bean
    Binding bindingExchangeMessage(Queue queueMessage, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).with(RabbitTopic.message);
    }

    //对列绑定并关联到ROUTINGKEY
    @Bean
    Binding bindingExchangeMessages(Queue queueMessages, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");//*表示一个词,#表示零个或多个词
    }



//    @Bean
//    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//                                             MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(RabbitTopic.message);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }

//    @Bean
//    MessageListenerAdapter listenerAdapter(Receiver receiver) {
//        return new MessageListenerAdapter(receiver, "receiveMessage");
//    }
}
