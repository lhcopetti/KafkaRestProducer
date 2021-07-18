package com.copetti.consumer;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
public class KafkaMessage {
    private String value;
    private Map<String, String> headers;
}
