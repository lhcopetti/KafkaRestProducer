package com.copetti.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class KafkaMessageConsumer {

    private Properties properties(String brokerList) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    public List<KafkaMessage> consumeMessages(String brokerList, String topic) {

        var props = properties(brokerList);
        var consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Collections.singletonList(topic));

        int maxDurationMillis = 10_000;
        int step = 500;

        int duration = 0;
        List<KafkaMessage> messages = new ArrayList<>();

        while(messages.isEmpty() && duration < maxDurationMillis) {
            duration += step;
            ConsumerRecords<String, String> poll = consumer.poll(Duration.ofMillis(step));

            poll.forEach(c -> {
                var msg = KafkaMessage.builder()
                    .value(c.value())
                    .build();
                messages.add(msg);
            });
        }
        return messages;
    }

}
