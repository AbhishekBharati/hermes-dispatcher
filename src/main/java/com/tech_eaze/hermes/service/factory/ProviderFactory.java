package com.tech_eaze.hermes.service.factory;

import com.tech_eaze.hermes.service.channel.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProviderFactory {
    private final Map<String, NotificationChannel> channelMap;

    public ProviderFactory(List<NotificationChannel> allChannels){
        this.channelMap = allChannels.stream()
                .collect(Collectors.toMap(
                        channel -> channel.getProviderCode().toUpperCase(),
                        channel -> channel
                ));
    }

    public NotificationChannel getProvider(String providerCode){
        if(providerCode == null || providerCode.trim().isEmpty()){
            throw new IllegalArgumentException("Provider can not be null or empty");
        }
        NotificationChannel provider = channelMap.get(providerCode.toUpperCase());
        if(provider == null){
            throw new IllegalArgumentException("Unsupported provider: " + providerCode);
        }
        return provider;
    }
}
