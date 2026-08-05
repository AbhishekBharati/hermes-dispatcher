package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.config.RabbitMQConfig;
import com.tech_eaze.hermes.domain.NotificationLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationPublisher publisher;

    @Test
    @DisplayName("Should publish email notification to correct exchange and EMAIL routing key")
    void publish_WithEmailChannel_ShouldPublishWithEmailRoutingKey() {
        NotificationLog log = NotificationLog.builder()
                .notificationId("notif-1001")
                .userId("usr-1")
                .eventType("WELCOME")
                .channel("EMAIL")
                .status(NotificationLog.Status.PENDING)
                .payload(Map.of("email", "test@example.com"))
                .retryCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        publisher.publish(log);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "EMAIL", log);
    }

    @Test
    @DisplayName("Should publish SMS notification to correct exchange and SMS routing key")
    void publish_WithSmsChannel_ShouldPublishWithSmsRoutingKey() {
        NotificationLog log = NotificationLog.builder()
                .notificationId("notif-1002")
                .userId("usr-2")
                .eventType("OTP")
                .channel("SMS")
                .status(NotificationLog.Status.PENDING)
                .payload(Map.of("phone", "+1234567890"))
                .build();

        publisher.publish(log);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "SMS", log);
    }

    @Test
    @DisplayName("Should publish webhook notification to correct exchange and WEBHOOK routing key")
    void publish_WithWebhookChannel_ShouldPublishWithWebhookRoutingKey() {
        NotificationLog log = NotificationLog.builder()
                .notificationId("notif-1003")
                .userId("usr-3")
                .eventType("ORDER_CREATED")
                .channel("WEBHOOK")
                .status(NotificationLog.Status.PENDING)
                .payload(Map.of("url", "https://example.com/webhook"))
                .build();

        publisher.publish(log);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "WEBHOOK", log);
    }

    @Test
    @DisplayName("Should handle null channel by passing null routing key to rabbitTemplate")
    void publish_WithNullChannel_ShouldPublishWithNullRoutingKey() {
        NotificationLog log = NotificationLog.builder()
                .notificationId("notif-1004")
                .channel(null)
                .build();

        publisher.publish(log);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, null, log);
    }

    @Test
    @DisplayName("Should propagate AmqpException when rabbitTemplate fails to publish")
    void publish_WhenRabbitTemplateThrowsAmqpException_ShouldPropagateException() {
        NotificationLog log = NotificationLog.builder()
                .notificationId("notif-1005")
                .channel("EMAIL")
                .build();

        doThrow(new AmqpException("Broker connection refused"))
                .when(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EXCHANGE_NAME), eq("EMAIL"), any(NotificationLog.class));

        assertThatThrownBy(() -> publisher.publish(log))
                .isInstanceOf(AmqpException.class)
                .hasMessage("Broker connection refused");

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "EMAIL", log);
    }
}