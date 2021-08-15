package com.copetti.controller;

import com.copetti.core.KafkaRestRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaProducerMapper {

    public static KafkaRestRequest mapFromRequest(final String brokerList, final PublishRequest request) {
        return new KafkaRestRequest(request.getKey(), request.getTopic(), brokerList, request.getValue(), request.getHeaders());
    }

}
