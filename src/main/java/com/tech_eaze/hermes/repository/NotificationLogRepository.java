package com.tech_eaze.hermes.repository;

import com.tech_eaze.hermes.domain.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
}
