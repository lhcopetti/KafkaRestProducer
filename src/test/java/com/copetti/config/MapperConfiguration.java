package com.copetti.config;

import com.copetti.controller.KafkaRestRequestMapper;
import com.copetti.controller.KafkaRestRequestMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MapperConfiguration {

    @Bean
    public KafkaRestRequestMapper kafkaRestRequestMapper() {
        return new KafkaRestRequestMapperImpl();
    }
}