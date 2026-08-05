package com.tech_eaze.hermes.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@Document(collection = "notification_logs")
public class NotificationLog {

    @Id
    private String notificationId;
    private String userId;
    private String eventType;
    private String channel;
    private Status status;
    private Map<String, Object> payload;
    private int retryCount;
    private Instant createdAt;
    private Instant updatedAt;

    public enum Status{
        PENDING, PROCESSING, SUCCESS, FAILED
    }
}
