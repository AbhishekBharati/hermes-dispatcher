package com.tech_eaze.hermes.consumer;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.service.NotificationConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationConsumerService consumerService;

    @RabbitListener(queues = "queue.email")
    public void processEmail(NotificationLog notificationLog){
        log.info("Received message on queue.email for ID: {}", notificationLog.getNotificationId());
        consumerService.processNotification(notificationLog);
    }

    @RabbitListener(queues = "queue.sms")
    public void processSms(NotificationLog notificationLog){
        log.info("Received message on queue.sms for ID: {}", notificationLog.getNotificationId());
        consumerService.processNotification(notificationLog);
    }

    @RabbitListener(queues = "queue.webhook")
    public void processWebhook(NotificationLog notificationLog){
        log.info("Received message on queue.webhook for ID: {}", notificationLog.getNotificationId());
        consumerService.processNotification(notificationLog);
    }
}
