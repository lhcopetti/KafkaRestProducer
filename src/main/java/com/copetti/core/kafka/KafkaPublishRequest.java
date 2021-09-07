package com.copetti.core.kafka;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class KafkaPublishRequest {
    @NonNull String brokerList;
    @NonNull String topic;
    @NonNull List<KafkaMessage> messages;
}
