package com.copetti.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KafkaProducerRequest {

    private final String topicName;
    private final String brokerList;
    private final Object value;
}
