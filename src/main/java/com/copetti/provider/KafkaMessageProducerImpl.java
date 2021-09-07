package com.copetti.provider;

import com.copetti.core.kafka.KafkaMessage;
import com.copetti.core.kafka.KafkaPublishRequest;
import com.copetti.exception.KafkaRecordCreationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class KafkaMessageProducerImpl implements KafkaMessageProducer {

    private final ObjectMapper mapper;

    public void publish(KafkaPublishRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        var props = properties(request.getBrokerList());
        try (var producer = new KafkaProducer<String, String>(props)) {
            val records = createRecords(request);
            sendKafkaRecords(producer, records);
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

    private List<ProducerRecord<String, String>> createRecords(final KafkaPublishRequest request) {
        val topic = request.getTopic();
        return request.getMessages().stream()
            .map(msg -> createKafkaRecord(topic, msg))
            .collect(Collectors.toList());
    }

    private ProducerRecord<String, String> createKafkaRecord(final String topic, final KafkaMessage message) throws KafkaRecordCreationException {
        try {
            val producerRecord = new ProducerRecord<>(topic, message.getKey(), mapper.writeValueAsString(message.getValue()));
            addKafkaHeaders(producerRecord, message);
            return producerRecord;
        } catch (JsonProcessingException e) {
            throw new KafkaRecordCreationException("Failed to serialize value as JSON", e);
        }
    }

    private void addKafkaHeaders(final ProducerRecord<String, String> producerRecord, final KafkaMessage message) {
        message.getHeaders().forEach((k, v) -> producerRecord.headers().add(k, v.getBytes()));
    }

    private <K, V> void sendKafkaRecords(final KafkaProducer<K, V> producer,
        final List<ProducerRecord<K, V>> messages) throws ExecutionException, InterruptedException, TimeoutException {

        val futures = messages.stream()
            .map(msg -> {
                val callback = new KafkaFutureCallback();
                producer.send(msg, callback);
                return callback;
            })
            .collect(Collectors.toList())
            .toArray(new CompletableFuture<?>[0]);

        CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);
    }

}
