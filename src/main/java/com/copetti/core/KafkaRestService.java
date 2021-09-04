package com.copetti.core;

import com.copetti.exception.InvalidRepeatValueException;
import com.copetti.provider.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class KafkaRestService {

    public static final String KAFKA_REST_PREFIX_TAG = "KafkaRest-";

    public static final String REPEAT_PUBLISH_TAG = KAFKA_REST_PREFIX_TAG + "Repeat";
    public static final int REPEAT_DEFAULT_VALUE = 1;

    public static final String RANDOM_UUID_TAG = "${UUID.randomUUID}";

    private final KafkaMessageProducer producer;

    public void publish(KafkaRestRequest request) throws InvalidRepeatValueException {
        var processed = processRequest(request);

        processed.stream()
            .parallel()
            .forEach(this::produce);
    }

    private void produce(final KafkaRestRequest request) {
        try {
            producer.publish(request);
        } catch (InterruptedException e) {
            log.error("The routine has been interrupted while publishing request | topic: {}, error: {}", request.getTopicName(), e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Exception occurred while publishing request | topic: {}, error: {}", request.getTopicName(), e.getMessage(), e);
        }
    }

    private List<KafkaRestRequest> processRequest(KafkaRestRequest request) throws InvalidRepeatValueException {
        var times = getRepeat(request);

        val requests = new ArrayList<KafkaRestRequest>();

        for (var i = 0; i < times; ++i) {
            val req = KafkaRestRequest.builder()
                .key(request.getKey())
                .value(request.getValue())
                .headers(processHeaders(request.getHeaders()))
                .brokerList(request.getBrokerList())
                .topicName(request.getTopicName())
                .build();
            requests.add(req);
        }

        return requests;
    }

    private int getRepeat(final KafkaRestRequest request) throws InvalidRepeatValueException {
        Map<String, String> headers = request.getHeaders();
        String repeat = headers.get(REPEAT_PUBLISH_TAG);

        if (null == repeat)
            return REPEAT_DEFAULT_VALUE;

        try {
            return Integer.parseInt(repeat);
        } catch (NumberFormatException e) {
            throw new InvalidRepeatValueException(repeat);
        }
    }

    private Map<String, String> processHeaders(final Map<String, String> headers) {
        Map<String, String> enriched = enrichHeader(headers);
        return stripKafkaRestHeaders(enriched);
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

    private Map<String, String> stripKafkaRestHeaders(final Map<String, String> enriched) {
        enriched.keySet().removeIf(key -> key.startsWith(KAFKA_REST_PREFIX_TAG));
        return enriched;
    }

}
