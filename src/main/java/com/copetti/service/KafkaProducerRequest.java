package com.copetti.service;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class KafkaProducerRequest {
    private final String key;
    private final String topicName;
    private final String brokerList;
    private final Object value;
    private final Map<String, String> headers;

    public KafkaProducerRequest(String key, String topic, String broker, Object value, Map<String, String> headers) {
        this.key = key;
        this.topicName = topic;
        this.brokerList = broker;
        this.value = value;
        this.headers = null == headers ? Collections.emptyMap() : headers;
    }
}
