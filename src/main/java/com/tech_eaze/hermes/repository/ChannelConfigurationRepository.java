package com.tech_eaze.hermes.repository;

import com.tech_eaze.hermes.domain.ChannelConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelConfigurationRepository extends MongoRepository<ChannelConfiguration, String> {
}
