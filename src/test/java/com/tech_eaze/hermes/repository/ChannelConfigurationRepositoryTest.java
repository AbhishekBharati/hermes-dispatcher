package com.tech_eaze.hermes.repository;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class ChannelConfigurationRepositoryTest {
    @Autowired
    private ChannelConfigurationRepository repository;

    @AfterEach
    void cleanUp(){
        repository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveChannelConfiguration() {
        ChannelConfiguration config = ChannelConfiguration.builder()
                .channelType("EMAIL")
                .activeProvider("AWS")
                .credentials(Map.of("accessKey", "fake-key", "region", "ap-south-1"))
                .build();

        repository.save(config);
        Optional<ChannelConfiguration> retrievedConfig = repository.findById("EMAIL");

        assertThat(retrievedConfig).isPresent();
        assertThat(retrievedConfig.get().getActiveProvider()).isEqualTo("AWS");
        assertThat(retrievedConfig.get().getCredentials()).containsEntry("region", "ap-south-1");
    }
}
