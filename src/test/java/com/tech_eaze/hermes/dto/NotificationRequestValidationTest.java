package com.tech_eaze.hermes.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations(){
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "USER_SIGNUP",
                "EMAIL",
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenChannelIsInvalid_thenViolationOccurs(){
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "USER_SIGNUP",
                "PIGEON_CARRIER",
                Map.of("message", "hello")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Channel must be EMAIL, SMS or WEBHOOK");
    }

    @Test
    void whenUserIdIsBlank_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                "   ",
                "USER_SIGNUP",
                "EMAIL",
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User ID can't be blank");
    }

    @Test
    void whenUserIdIsNull_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                null,
                "USER_SIGNUP",
                "EMAIL",
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User ID can't be blank");
    }

    @Test
    void whenEventTypeIsBlank_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "",
                "EMAIL",
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Event Type can't be blank");
    }

    @Test
    void whenEventTypeIsNull_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                null,
                "EMAIL",
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Event Type can't be blank");
    }

    @Test
    void whenChannelIsBlank_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "USER_SIGNUP",
                "  ",
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenChannelIsNull_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "USER_SIGNUP",
                null,
                Map.of("to", "test@example.com")
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Channel must be specified");
    }

    @Test
    void whenChannelIsSmsOrWebhook_thenNoViolations() {
        NotificationRequest smsRequest = new NotificationRequest(
                "usr_123",
                "OTP",
                "SMS",
                Map.of("phone", "+1234567890")
        );

        NotificationRequest webhookRequest = new NotificationRequest(
                "usr_123",
                "ORDER_CREATED",
                "WEBHOOK",
                Map.of("url", "https://example.com/webhook")
        );

        assertThat(validator.validate(smsRequest)).isEmpty();
        assertThat(validator.validate(webhookRequest)).isEmpty();
    }

    @Test
    void whenPayloadIsNull_thenViolationOccurs() {
        NotificationRequest request = new NotificationRequest(
                "usr_123",
                "USER_SIGNUP",
                "EMAIL",
                null
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Payload can't be null");
    }

    @Test
    void whenMultipleFieldsInvalid_thenMultipleViolationsOccur() {
        NotificationRequest request = new NotificationRequest(
                "",
                "",
                "INVALID_CHANNEL",
                null
        );

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(4);
    }
}
