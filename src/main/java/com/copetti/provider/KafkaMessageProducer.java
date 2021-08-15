package com.copetti.provider;

import com.copetti.core.KafkaRestRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Component
public class KafkaMessageProducer {

    private final ObjectMapper mapper;

    public void publish(KafkaRestRequest request) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        var props = properties(request.getBrokerList());
        try (var producer = new KafkaProducer<String, String>(props)) {
            produceMessage(producer, request);
        }
    }

    private static Properties properties(String broker) {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5000");
        return properties;
    }

    private void produceMessage(final KafkaProducer<String, String> producer,
        final KafkaRestRequest request) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        ProducerRecord<String, String> message = createKafkaRecord(request);
        addKafkaHeaders(message, request);
        sendKafkaRecord(producer, message);
    }

    private ProducerRecord<String, String> createKafkaRecord(final KafkaRestRequest request) throws JsonProcessingException {
        return new ProducerRecord<>(request.getTopicName(), request.getKey(), mapper.writeValueAsString(request.getValue()));
    }

    private <K, V> void addKafkaHeaders(final ProducerRecord<K, V> message, final KafkaRestRequest request) {
        request.getHeaders().forEach((k, v) -> message.headers().add(k, v.getBytes()));
    }

    private <K, V> void sendKafkaRecord(final KafkaProducer<K, V> producer,
        final ProducerRecord<K, V> message) throws ExecutionException, InterruptedException, TimeoutException {
        producer.send(message).get(10, TimeUnit.SECONDS);
    }

}
