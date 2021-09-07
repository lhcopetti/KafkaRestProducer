package com.copetti.core;

import com.copetti.core.kafka.KafkaPublishRequest;
import com.copetti.exception.InvalidRepeatValueException;
import com.copetti.provider.KafkaMessageProducer;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.copetti.core.KafkaRestService.REPEAT_PUBLISH_TAG;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaRestServiceTest {

    private @InjectMocks KafkaRestService service;

    private @Mock KafkaMessageProducer producer;

    private @Captor ArgumentCaptor<KafkaPublishRequest> captor;

    @Test
    void givenHeaderWithRandomUUIDRequest_ExpectARandomUUIDToBeGenerated() throws Exception {
        var req = minimalRequest()
            .header("any-header", KafkaRestService.RANDOM_UUID_TAG)
            .build();

        service.publish(req);

        verify(producer).publish(captor.capture());
        val captured = captor.getValue();
        var validRandomUUID = captured.getMessage().getHeaders().get("any-header");
        assertDoesNotThrow(() -> UUID.fromString(validRandomUUID));
    }

    @Test
    void givenRandomUUIDHeaderAndRepeatGreaterThanOne_ExpectDifferentUUIDForEachPublish() throws Exception {
        var req = minimalRequest()
            .header("any-header", KafkaRestService.RANDOM_UUID_TAG)
            .header(REPEAT_PUBLISH_TAG, "2")
            .build();

        service.publish(req);

        verify(producer, times(2)).publish(captor.capture());
        val captured = captor.getAllValues();

        var idFirst = captured.get(0).getMessage().getHeaders().get("any-header");
        var idSecond = captured.get(1).getMessage().getHeaders().get("any-header");
        assertNotEquals(idFirst, idSecond);
    }

    @Test
    void givenHeaderWithRepeatInstruction_ExpectPublishToBeCalledThatManyTimes() throws Exception {
        var req = minimalRequest()
            .header(REPEAT_PUBLISH_TAG, "3")
            .build();

        service.publish(req);

        verify(producer, times(3)).publish(captor.capture());
        val captured = captor.getValue();
        assertNull(captured.getMessage().getHeaders().get(REPEAT_PUBLISH_TAG));
    }

    @Test
    void givenHeaderWithRepeatInstructionWithInvalidValue_ExpectExplicitInvalidRepeatValueException() throws Exception {
        var req = KafkaRestRequest.builder()
            .header(REPEAT_PUBLISH_TAG, "this-is-not-a-number")
            .build();

        assertThrows(InvalidRepeatValueException.class, () -> service.publish(req));

        verify(producer, never()).publish(captor.capture());
    }

    private KafkaRestRequest.KafkaRestRequestBuilder minimalRequest() {
        return KafkaRestRequest.builder()
            .topicName("the-topic")
            .brokerList("the-broker")
            .value("the-value");
    }
}