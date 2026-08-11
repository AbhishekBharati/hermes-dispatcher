package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRetryService {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationLogRepository logRepository;

    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_BACKOFF_MS = 2000; // 2 seconds

    public void handleRetry(NotificationLog notificationLog){
        int currentRetry = notificationLog.getRetryCount();
        if(currentRetry >= MAX_RETRIES){
            log.error("Notification {} exceeded max retries. Routing to DLQ", notificationLog.getNotificationId());
            notificationLog.setStatus(NotificationLog.Status.FAILED);
            notificationLog.setUpdatedAt(Instant.now());
            logRepository.save(notificationLog);
            return;
        }

        int nextRetryCount = currentRetry + 1;
        notificationLog.setRetryCount(nextRetryCount);
        notificationLog.setStatus(NotificationLog.Status.PROCESSING);

        logRepository.save(notificationLog);

        long delayMS = INITIAL_BACKOFF_MS * (long) Math.pow(2, currentRetry);

        log.warn("Retrying notification {} (Attempt {}/{}). Delaying for {} ms.",
                notificationLog.getNotificationId(), nextRetryCount, MAX_RETRIES, delayMS);

        rabbitTemplate.convertAndSend(
                "notification.retry",
                notificationLog.getChannel(),
                notificationLog,
                message -> {
                    message.getMessageProperties().setDelayLong(delayMS);
                    return message;
                }
        );
    }
}