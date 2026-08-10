package com.tech_eaze.hermes.service;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import com.tech_eaze.hermes.repository.ChannelConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelConfigurationService {

    private final ChannelConfigurationRepository configRepository;

    @Cacheable(value = "channelConfigs", key = "#channelType")
    public ChannelConfiguration getActiveConfiguration(String channelType){
        log.info("CACHE MISS : Hitting MongoDB");
        return configRepository.findById(channelType)
                .orElseThrow(() -> new IllegalArgumentException("No Dashboard configuration found for channel: " + channelType));
    }

    @CacheEvict(value = "channelConfigs", key = "#channelType")
    public void clearConfigCache(String channelType){
        log.info("Cache cleared for channel : {}", channelType);
    }
}
