package com.copetti.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaProducerRequestTest {

    @Test
    public void givenNullHeaders_expectEmptyMapToBeReturned() {
        KafkaProducerRequest req = new KafkaProducerRequest("a", "b", "value", null);
        assertThat(req.getHeaders()).isNotNull();
    }
}