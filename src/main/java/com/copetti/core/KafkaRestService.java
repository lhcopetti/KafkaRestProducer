package com.copetti.core;

import com.copetti.provider.KafkaMessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class KafkaRestService {

    private static final String RANDOM_UUID_TAG = "${UUID.randomUUID}";

    private final KafkaMessageProducer producer;

    public void publish(KafkaRestRequest request) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        var enriched = enrichRequest(request);
        producer.publish(enriched);
    }

    private KafkaRestRequest enrichRequest(KafkaRestRequest request) {
        return KafkaRestRequest.builder()
            .key(request.getKey())
            .value(request.getValue())
            .headers(enrichHeader(request.getHeaders()))
            .brokerList(request.getBrokerList())
            .topicName(request.getTopicName())
            .build();
    }

    private Map<String, String> enrichHeader(final Map<String, String> headers) {
        return headers.entrySet().stream()
            .map(this::enrichHeader)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, String> enrichHeader(Map.Entry<String, String> entry) {
        if (!RANDOM_UUID_TAG.equals(entry.getValue()))
            return entry;

        var uuid = UUID.randomUUID().toString();
        log.info("Generating an UUID for header named '{}' = {}", entry.getKey(), uuid);
        return Map.entry(entry.getKey(), UUID.randomUUID().toString());
    }

}
