package com.tech_eaze.hermes.service.channel;

import java.util.Map;

public interface NotificationChannel {
    // Universal Execn method that All providers must implement :-
    // @param payload :- The dynamic data to send (e.g. {"to" : "test@test.com", "subject" : "Hello"}
    // @param credentials The API keys retrived from the channelConfig MongoDB docs.
    // @return boolean :- True if API call succeeded, false if it is failed.
    boolean send(Map<String, Object> payload, Map<String, String> credentials);
    String getProviderCode();
}
