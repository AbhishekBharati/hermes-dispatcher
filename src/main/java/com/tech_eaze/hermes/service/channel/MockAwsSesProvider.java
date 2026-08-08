package com.tech_eaze.hermes.service.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MockAwsSesProvider implements NotificationChannel{
    @Override
    public boolean send(Map<String, Object> payload, Map<String, String> credentials){
        log.info("===========================================");
        log.info(" Executing AWS SES Mock Provider");
        log.info("Credentials Injected : {}", credentials);
        log.info("Payload processing : {}", payload);
        log.info("Satus: SUCCESS");
        log.info("===========================================");

        return true;
    }

    @Override
    public String getProviderCode(){
        return "AWS";
    }
}
