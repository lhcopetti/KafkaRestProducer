package com.copetti.core;

import com.copetti.provider.KafkaMessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaRestServiceTest {

    private @InjectMocks KafkaRestService service;

    private @Mock KafkaMessageProducer producer;

    private @Captor ArgumentCaptor<KafkaRestRequest> captor;

    @Test
    void givenHeaderWithRandomUUIDRequest_ExpectARandomUUIDToBeGenerated() throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        var req = KafkaRestRequest.builder()
            .header("any-header", "${UUID.randomUUID}")
            .build();

        service.publish(req);

        verify(producer).publish(captor.capture());
        KafkaRestRequest captured = captor.getValue();
        var validRandomUUID = captured.getHeaders().get("any-header");
        assertDoesNotThrow(() -> UUID.fromString(validRandomUUID));
    }
}