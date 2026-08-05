package com.tech_eaze.hermes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Map;

public record NotificationRequest(
        @NotBlank(message = "User ID can't be blank")
        String userId,

        @NotBlank(message = "Event Type can't be blank")
        String eventType,

        @NotBlank(message = "Channel must be specified")
        @Pattern(regexp = "^(EMAIL|SMS|WEBHOOK)$", message = "Channel must be EMAIL, SMS or WEBHOOK")
        String channel,

        @NotNull(message = "Payload can't be null")
        Map<String, Object> payload
) {
}
