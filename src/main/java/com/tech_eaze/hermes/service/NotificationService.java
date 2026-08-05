package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.dto.NotificationRequest;
import com.tech_eaze.hermes.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository repository;
    private final NotificationPublisher publisher;

    public NotificationLog processNotification(NotificationRequest request){
        NotificationLog pendingLog = NotificationLog.builder()
                .notificationId(UUID.randomUUID().toString())
                .userId(request.userId())
                .eventType(request.eventType())
                .channel(request.channel())
                .status(NotificationLog.Status.PENDING)
                .payload(request.payload())
                .retryCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        NotificationLog savedLog = repository.save(pendingLog);
        publisher.publish(savedLog);
        return savedLog;
    }
}
