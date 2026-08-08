package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.repository.ChannelConfigurationRepository;
import com.tech_eaze.hermes.service.channel.NotificationChannel;
import com.tech_eaze.hermes.service.factory.ProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumerService {
    private final ChannelConfigurationRepository configRepository;
    private final ProviderFactory providerFactory;

    public void processNotification(NotificationLog notificationLog){
        log.info("Processing notification ID: {}", notificationLog.getNotificationId());
        ChannelConfiguration config = configRepository.findById(notificationLog.getChannel())
                .orElseThrow(() -> new IllegalStateException("No Dashboard configuration found for channel: " + notificationLog.getChannel()));

        NotificationChannel provider = providerFactory.getProvider(config.getActiveProvider());
        log.info("Routing notification through {} provider", provider.getProviderCode());
        boolean isSuccess = provider.send(notificationLog.getPayload(), config.getCredentials());
        if(isSuccess){
            log.info("Notification {} successfully processed", notificationLog.getNotificationId());
        } else {
            log.error("Notification {} failed to process", notificationLog.getNotificationId());
        }
    }
}
