package com.copetti.controller;

import com.copetti.service.KafkaProducerRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaProducerMapper {

    public static KafkaProducerRequest mapFromRequest(final String brokerList, final PublishRequest request) {
        return new KafkaProducerRequest(request.getKey(), request.getTopic(), brokerList, request.getValue(), request.getHeaders());
    }

}
