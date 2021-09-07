package com.copetti.core.kafka;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class KafkaPublishRequest {
    @NonNull String brokerList;
    @NonNull String topic;
    @NonNull KafkaMessage message;
}
