package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.NotificationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.tech_eaze.hermes.config.RabbitMQConfig.EXCHANGE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationLog notificationLog){
        String routingKey = notificationLog.getChannel();

        log.info("Publishing notification [{}] to exchange [{}] with routing key [{}]",
                notificationLog.getNotificationId(), EXCHANGE_NAME, notificationLog.getChannel());

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, notificationLog);
    }
}
