package com.copetti.core.kafka;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class KafkaMessage {

    String key;
    @NonNull Object value;
    @NonNull Map<String, String> headers;
}
