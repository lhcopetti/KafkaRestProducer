package com.copetti.service;

import com.copetti.core.KafkaRestRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaProducerRequestTest {

    @Test
    void givenNullHeaders_expectEmptyMapToBeReturned() {
        KafkaRestRequest req = new KafkaRestRequest(null, "a", "b", "value", null);
        assertThat(req.getHeaders()).isNotNull();
    }
}