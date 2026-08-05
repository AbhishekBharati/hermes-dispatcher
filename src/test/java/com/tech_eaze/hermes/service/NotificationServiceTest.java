package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.dto.NotificationRequest;
import com.tech_eaze.hermes.repository.NotificationLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationLogRepository repository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should correctly map all NotificationRequest fields and save pending log")
    void logPendingNotification_ShouldMapAndSaveCorrectly() {
        NotificationRequest request = new NotificationRequest(
                "usr_999",
                "PASSWORD_RESET",
                "EMAIL",
                Map.of("email", "test@example.com", "token", "abc123token")
        );

        when(repository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationLog result = notificationService.logPendingNotification(request);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(repository).save(captor.capture());

        NotificationLog savedLog = captor.getValue();

        assertThat(savedLog.getNotificationId()).isNotNull();
        assertThat(UUID.fromString(savedLog.getNotificationId())).isNotNull(); // Validates UUID format
        assertThat(savedLog.getUserId()).isEqualTo("usr_999");
        assertThat(savedLog.getEventType()).isEqualTo("PASSWORD_RESET");
        assertThat(savedLog.getChannel()).isEqualTo("EMAIL");
        assertThat(savedLog.getStatus()).isEqualTo(NotificationLog.Status.PENDING);
        assertThat(savedLog.getPayload()).isEqualTo(Map.of("email", "test@example.com", "token", "abc123token"));
        assertThat(savedLog.getRetryCount()).isZero();
        assertThat(savedLog.getCreatedAt()).isNotNull();
        assertThat(savedLog.getUpdatedAt()).isNotNull();
        assertThat(savedLog.getUpdatedAt()).isAfterOrEqualTo(savedLog.getCreatedAt());

        assertThat(result).isSameAs(savedLog);
    }

    @Test
    @DisplayName("Should generate unique notification IDs for consecutive calls")
    void logPendingNotification_ShouldGenerateUniqueNotificationIds() {
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "WELCOME",
                "EMAIL",
                Map.of("email", "user@example.com")
        );

        when(repository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationLog log1 = notificationService.logPendingNotification(request);
        NotificationLog log2 = notificationService.logPendingNotification(request);

        assertThat(log1.getNotificationId()).isNotEqualTo(log2.getNotificationId());
    }

    @Test
    @DisplayName("Should handle SMS and WEBHOOK channels correctly")
    void logPendingNotification_WithDifferentChannels_ShouldMapCorrectly() {
        NotificationRequest smsRequest = new NotificationRequest(
                "usr_456",
                "OTP_VERIFICATION",
                "SMS",
                Map.of("phoneNumber", "+15550199", "code", "654321")
        );

        when(repository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationLog smsLog = notificationService.logPendingNotification(smsRequest);

        assertThat(smsLog.getChannel()).isEqualTo("SMS");
        assertThat(smsLog.getEventType()).isEqualTo("OTP_VERIFICATION");
        assertThat(smsLog.getPayload()).containsEntry("phoneNumber", "+15550199");
    }

    @Test
    @DisplayName("Should propagate exception when repository save fails")
    void logPendingNotification_WhenRepositoryThrowsException_ShouldPropagateException() {
        NotificationRequest request = new NotificationRequest(
                "usr_999",
                "PASSWORD_RESET",
                "EMAIL",
                Map.of("email", "test@example.com")
        );

        when(repository.save(any(NotificationLog.class)))
                .thenThrow(new RuntimeException("Database connectivity failure"));

        assertThatThrownBy(() -> notificationService.logPendingNotification(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connectivity failure");

        verify(repository).save(any(NotificationLog.class));
    }
}

