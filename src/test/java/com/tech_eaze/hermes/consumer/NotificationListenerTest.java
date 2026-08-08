package com.tech_eaze.hermes.consumer;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.service.NotificationConsumerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationListenerTest {
    @Mock
    private NotificationConsumerService consumerService;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void shouldProcessEmailMessage(){
        NotificationLog mockLog = NotificationLog.builder()
                .notificationId(UUID.randomUUID().toString())
                .channel("EMAIL")
                .build();

        notificationListener.processEmail(mockLog);

        verify(consumerService, times(1)).processNotification(mockLog);
    }
}
