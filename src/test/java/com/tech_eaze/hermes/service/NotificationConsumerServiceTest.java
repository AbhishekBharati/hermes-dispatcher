package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.repository.ChannelConfigurationRepository;
import com.tech_eaze.hermes.service.channel.NotificationChannel;
import com.tech_eaze.hermes.service.factory.ProviderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationConsumerServiceTest {
    @Mock
    private ChannelConfigurationService configService;

    @Mock
    private ProviderFactory providerFactory;

    @Mock
    private NotificationChannel mockProvider;

    @InjectMocks
    private NotificationConsumerService consumerService;

    @Test
    void shouldProcessNotification_WhenConfigurationExists(){
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

        verify(configService, times(1)).getActiveConfiguration("EMAIL");
        verify(providerFactory, times(1)).getProvider("AWS");
        verify(mockProvider, times(1)).send(mockLog.getPayload(), mockConfig.getCredentials());
    }

    @Test
    void shouldThrowException_WhenNoConfigurationFound(){
        NotificationLog mockLog = NotificationLog.builder()
                .channel("UNKNOWN_CHANNEL")
                .build();

        when(configService.getActiveConfiguration("UNKNOWN_CHANNEL"))
                .thenThrow(new IllegalArgumentException("No Dashboard configuration found for channel: UNKNOWN_CHANNEL"));

        assertThatThrownBy(() -> consumerService.processNotification(mockLog))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No Dashboard configuration found for channel: UNKNOWN_CHANNEL");

        verifyNoInteractions(providerFactory);
    }
}