package com.tech_eaze.hermes.api.controller;

import com.tech_eaze.hermes.domain.NotificationLog;
import com.tech_eaze.hermes.dto.NotificationRequest;
import com.tech_eaze.hermes.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    @DisplayName("Should return 202 Accepted when valid EMAIL notification request is submitted")
    void submitNotification_WhenValidEmailPayload_ShouldReturn202Accepted() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_123", "SIGNUP", "EMAIL", Map.of("email", "test@example.com")
        );

        String generatedId = UUID.randomUUID().toString();
        NotificationLog mockedLog = NotificationLog.builder()
                .notificationId(generatedId)
                .build();

        when(notificationService.processNotification(any(NotificationRequest.class))).thenReturn(mockedLog);

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.notificationId").value(generatedId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Notification Accepted and Queued for Processing"));

        verify(notificationService).processNotification(any(NotificationRequest.class));
    }

    @Test
    @DisplayName("Should return 202 Accepted when valid SMS notification request is submitted")
    void submitNotification_WhenValidSmsPayload_ShouldReturn202Accepted() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_456", "OTP", "SMS", Map.of("phone", "+1234567890")
        );

        NotificationLog mockedLog = NotificationLog.builder()
                .notificationId("notif-sms-1")
                .build();

        when(notificationService.processNotification(any(NotificationRequest.class))).thenReturn(mockedLog);

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.notificationId").value("notif-sms-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(notificationService).processNotification(any(NotificationRequest.class));
    }

    @Test
    @DisplayName("Should return 202 Accepted when valid WEBHOOK notification request is submitted")
    void submitNotification_WhenValidWebhookPayload_ShouldReturn202Accepted() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_789", "PAYMENT_SUCCESS", "WEBHOOK", Map.of("url", "https://example.com/callback")
        );

        NotificationLog mockedLog = NotificationLog.builder()
                .notificationId("notif-wh-1")
                .build();

        when(notificationService.processNotification(any(NotificationRequest.class))).thenReturn(mockedLog);

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.notificationId").value("notif-wh-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(notificationService).processNotification(any(NotificationRequest.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when userId is blank")
    void submitNotification_WhenUserIdIsBlank_ShouldReturn400BadRequest() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "", "SIGNUP", "EMAIL", Map.of("email", "test@example.com")
        );

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.invalidParams.userId").value("User ID can't be blank"));

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when eventType is blank")
    void submitNotification_WhenEventTypeIsBlank_ShouldReturn400BadRequest() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_123", "   ", "EMAIL", Map.of("email", "test@example.com")
        );

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.invalidParams.eventType").value("Event Type can't be blank"));

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when channel is invalid")
    void submitNotification_WhenChannelIsInvalid_ShouldReturn400BadRequest() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_123", "SIGNUP", "PUSH", Map.of("token", "xyz")
        );

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.invalidParams.channel").value("Channel must be EMAIL, SMS or WEBHOOK"));

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when payload is null")
    void submitNotification_WhenPayloadIsNull_ShouldReturn400BadRequest() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_123", "SIGNUP", "EMAIL", null
        );

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.invalidParams.payload").value("Payload can't be null"));

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when service throws exception")
    void submitNotification_WhenServiceThrowsException_ShouldReturnServerError() throws Exception {
        NotificationRequest request = new NotificationRequest(
                "usr_123", "SIGNUP", "EMAIL", Map.of("email", "test@example.com")
        );

        when(notificationService.processNotification(any(NotificationRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(notificationService).processNotification(any(NotificationRequest.class));
    }
}