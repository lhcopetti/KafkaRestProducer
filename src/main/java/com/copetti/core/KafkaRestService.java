package com.copetti.core;

import com.copetti.provider.KafkaMessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class KafkaRestService {

    private final KafkaMessageProducer producer;

    public void publish(KafkaRestRequest request) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        producer.publish(request);
    }

}
