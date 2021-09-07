package com.copetti.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class KafkaFutureCallback implements Callback {

    private final CompletableFuture<RecordMetadata> future;

    public KafkaFutureCallback() {
        this(new CompletableFuture<>());
    }

    @Override
    public void onCompletion(final RecordMetadata recordMetadata, final Exception e) {
        if (null != e) {
            future.completeExceptionally(e);
            return;
        }

        future.complete(recordMetadata);
    }

}
