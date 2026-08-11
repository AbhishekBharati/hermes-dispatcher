package com.tech_eaze.hermes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "notification.direct";
    public static final String RETRY_EXCHANGE_NAME = "notification.retry";
    public static final String EMAIL_QUEUE = "queue.email";
    public static final String SMS_QUEUE = "queue.sms";
    public static final String WEBHOOK_QUEUE = "queue.webhook";

    @Bean
    public DirectExchange mainExchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue emailQueue(){
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue smsQueue(){
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public Queue webhookQueue(){
        return new Queue(WEBHOOK_QUEUE, true);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange mainExchange){
        return BindingBuilder.bind(emailQueue).to(mainExchange).with("EMAIL");
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, DirectExchange mainExchange){
        return BindingBuilder.bind(smsQueue).to(mainExchange).with("SMS");
    }

    @Bean
    public Binding webhookBinding(Queue webhookQueue, DirectExchange mainExchange){
        return BindingBuilder.bind(webhookQueue).to(mainExchange).with("WEBHOOK");
    }

    @Bean
    public CustomExchange retryExchange(){
        Map<String, Object> args = new HashMap<>();

        args.put("x-delayed-type", "direct");

        return new CustomExchange(RETRY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding emailRetryBinding(Queue emailQueue, CustomExchange retryExchange){
        return BindingBuilder.bind(emailQueue).to(retryExchange).with("EMAIL").noargs();
    }

    @Bean
    public Binding smsRetryBinding(Queue smsQueue, CustomExchange retryExchange){
        return BindingBuilder.bind(smsQueue).to(retryExchange).with("SMS").noargs();
    }

    @Bean
    public Binding webhookRetryBinding(Queue webhookQueue, CustomExchange retryExchange){
        return BindingBuilder.bind(webhookQueue).to(retryExchange).with("WEBHOOK").noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper){
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
