package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.repository.NotificationLogRepository;
import com.tech_eaze.hermes.service.channel.NotificationChannel;
import com.tech_eaze.hermes.service.factory.ProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumerService {
    private final ChannelConfigurationService configService;
    private final ProviderFactory providerFactory;
    private final NotificationLogRepository logRepository;
    private final NotificationRetryService retryService;

    public void processNotification(NotificationLog notificationLog){
        log.info("Processing notification ID: {}", notificationLog.getNotificationId());
        try {
            ChannelConfiguration config = configService.getActiveConfiguration(notificationLog.getChannel());

            NotificationChannel provider = providerFactory.getProvider(config.getActiveProvider());
            log.info("Routing notification through {} provider", provider.getProviderCode());
            boolean isSuccess = provider.send(notificationLog.getPayload(), config.getCredentials());
            if(isSuccess){
                notificationLog.setStatus(NotificationLog.Status.SUCCESS);
                log.info("Notification {} successfully processed", notificationLog.getNotificationId());
            } else {
                log.error("Notification {} failed to process", notificationLog.getNotificationId());
                retryService.handleRetry(notificationLog);
            }
        } catch (Exception e) {
            log.error("Critical failure processing notification {}: {}", notificationLog.getNotificationId(), e.getMessage());
            retryService.handleRetry(notificationLog);
        } finally {
            notificationLog.setUpdatedAt(Instant.now());
            if(notificationLog.getStatus() == NotificationLog.Status.SUCCESS){
                logRepository.save(notificationLog);
            }
        }
    }
}
