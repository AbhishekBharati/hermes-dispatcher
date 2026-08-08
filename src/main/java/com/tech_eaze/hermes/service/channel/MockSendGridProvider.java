package com.tech_eaze.hermes.service.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MockSendGridProvider implements NotificationChannel {

    @Override
    public boolean send(Map<String, Object> payload, Map<String, String> credentials){
        log.info("=============================");
        log.info(" EXECUTING SENDGRID MOCK PROVIDER");
        log.info("Credentials injected: {}", credentials);
        log.info("Payload processing: {}", payload);
        log.info("Status: SUCCESS");

        return true;
    }

    @Override
    public String getProviderCode(){
        return "SENDGRID";
    }
}