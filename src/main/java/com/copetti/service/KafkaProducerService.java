package com.copetti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaProducerService {

    private static Properties properties() {
        var properties = new Properties();
        String broker = "localhost:9092";
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5000");
        return properties;
    }

    public void publish(String topic) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        var producer = new KafkaProducer<String, String>(properties());
        ObjectMapper mapper = new ObjectMapper();
        try {
            var record = new ProducerRecord<String, String>(topic, null, mapper.writeValueAsString("the message to send"));
            producer.send(record).get(10, TimeUnit.SECONDS);
        } finally {
            producer.close();
        }
    }

}
