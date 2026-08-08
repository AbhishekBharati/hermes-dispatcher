package com.tech_eaze.hermes.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "channel_configurations")
public class ChannelConfiguration {
    @Id
    private String channelType;
    private String activeProvider;
    private Map<String,String> credentials;
    @LastModifiedDate
    private Instant updatedAt;
}
