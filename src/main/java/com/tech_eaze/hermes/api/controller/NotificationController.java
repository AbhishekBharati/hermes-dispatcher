package com.tech_eaze.hermes.api.controller;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.dto.NotificationRequest;
import com.tech_eaze.hermes.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Map<String, String>> submitNotification(@Valid @RequestBody NotificationRequest notificationRequest){
        NotificationLog savedLog = notificationService.processNotification(notificationRequest);

        return ResponseEntity.accepted().body(Map.of(
                "notificationId", savedLog.getNotificationId(),
                "status", "PENDING",
                "message", "Notification Accepted and Queued for Processing"
        ));
    }
}
