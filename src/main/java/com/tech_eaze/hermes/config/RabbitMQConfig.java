package com.tech_eaze.hermes.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "notification.direct";
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
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper){
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
