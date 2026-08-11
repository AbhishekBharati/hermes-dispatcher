package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.repository.NotificationLogRepository;
import com.tech_eaze.hermes.service.channel.NotificationChannel;
import com.tech_eaze.hermes.service.factory.ProviderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationConsumerServiceTest {

    @Mock
    private ChannelConfigurationService configService;

    @Mock
    private ProviderFactory providerFactory;

    @Mock
    private NotificationLogRepository logRepository;

    @Mock
    private NotificationRetryService retryService;

    @Mock
    private NotificationChannel mockProvider;

    @InjectMocks
    private NotificationConsumerService consumerService;

    @Test
    @DisplayName("Should set status to SUCCESS and save log when configuration exists and send succeeds")
    void shouldProcessNotification_WhenConfigurationExists() {
        NotificationLog mockLog = NotificationLog.builder()
                .notificationId(UUID.randomUUID().toString())
                .channel("EMAIL")
                .payload(Map.of("to", "test@example.com"))
                .build();

        ChannelConfiguration mockConfig = ChannelConfiguration.builder()
                .channelType("EMAIL")
                .activeProvider("AWS")
                .credentials(Map.of("apiKey", "12345"))
                .build();

        when(configService.getActiveConfiguration("EMAIL")).thenReturn(mockConfig);
        when(providerFactory.getProvider("AWS")).thenReturn(mockProvider);
        when(mockProvider.send(anyMap(), anyMap())).thenReturn(true);

        consumerService.processNotification(mockLog);

        assertThat(mockLog.getStatus()).isEqualTo(NotificationLog.Status.SUCCESS);
        assertThat(mockLog.getUpdatedAt()).isNotNull();

        verify(configService, times(1)).getActiveConfiguration("EMAIL");
        verify(providerFactory, times(1)).getProvider("AWS");
        verify(mockProvider, times(1)).send(mockLog.getPayload(), mockConfig.getCredentials());
        verify(logRepository, times(1)).save(mockLog);
        verifyNoInteractions(retryService);
    }

    @Test
    @DisplayName("Should trigger retryService when provider send fails")
    void shouldSetStatusToFailed_WhenProviderSendFails() {
        NotificationLog mockLog = NotificationLog.builder()
                .notificationId(UUID.randomUUID().toString())
                .channel("EMAIL")
                .payload(Map.of("to", "test@example.com"))
                .build();

        ChannelConfiguration mockConfig = ChannelConfiguration.builder()
                .channelType("EMAIL")
                .activeProvider("AWS")
                .credentials(Map.of("apiKey", "12345"))
                .build();

        when(configService.getActiveConfiguration("EMAIL")).thenReturn(mockConfig);
        when(providerFactory.getProvider("AWS")).thenReturn(mockProvider);
        when(mockProvider.send(anyMap(), anyMap())).thenReturn(false);

        consumerService.processNotification(mockLog);

        verify(retryService, times(1)).handleRetry(mockLog);
        verify(logRepository, never()).save(mockLog);
    }

    @Test
    @DisplayName("Should trigger retryService when configuration is missing or exception occurs")
    void shouldHandleException_WhenNoConfigurationFound() {
        NotificationLog mockLog = NotificationLog.builder()
                .notificationId(UUID.randomUUID().toString())
                .channel("UNKNOWN_CHANNEL")
                .build();

        when(configService.getActiveConfiguration("UNKNOWN_CHANNEL"))
                .thenThrow(new IllegalArgumentException("No Dashboard configuration found for channel: UNKNOWN_CHANNEL"));

        consumerService.processNotification(mockLog);

        verifyNoInteractions(providerFactory);
        verify(retryService, times(1)).handleRetry(mockLog);
        verify(logRepository, never()).save(mockLog);
    }
}