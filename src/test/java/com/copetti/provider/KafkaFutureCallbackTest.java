package com.copetti.provider;

import lombok.val;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaFutureCallbackTest {

    @InjectMocks
    private KafkaFutureCallback callback;

    @Mock
    private CompletableFuture<RecordMetadata> future;


    @Test
    void whenCallbackOnCompletionSuccessfully_ExpectFutureToComplete() {
        val metadata = new RecordMetadata(new TopicPartition("a", 1), 1L, 1L, 1L, 1L, 1, 1);
        callback.onCompletion(metadata, null);
        verify(future).complete(metadata);
    }

    @Test
    void whenCallbackOnCompletionWithThrowable_ExpectCompleteExceptionally() {
        val throwable = new RuntimeException("did not complete normally");
        callback.onCompletion(null, throwable);
        verify(future).completeExceptionally(throwable);
    }

}