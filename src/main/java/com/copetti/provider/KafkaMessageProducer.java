package com.copetti.provider;

import com.copetti.core.kafka.KafkaPublishRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface KafkaMessageProducer {

    void publish(KafkaPublishRequest request) throws ExecutionException, InterruptedException, TimeoutException;
}
