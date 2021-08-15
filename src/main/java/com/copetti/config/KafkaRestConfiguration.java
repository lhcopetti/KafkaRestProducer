package com.copetti.config;

import com.copetti.core.KafkaRestService;
import com.copetti.provider.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaRestConfiguration {

    private final KafkaMessageProducer producer;

    @Bean
    public KafkaRestService kafkaRestService() {
        return new KafkaRestService(producer);
    }
}
