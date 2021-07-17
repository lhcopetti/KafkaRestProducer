package com.copetti.producer;

import com.copetti.service.KafkaProducerRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaProducerMapper {

    public static KafkaProducerRequest mapFromRequest(final String topic, final String brokerList, final PublishRequest request) {
        return new KafkaProducerRequest(topic, brokerList, request.getValue(), request.getHeaders());
    }

}
