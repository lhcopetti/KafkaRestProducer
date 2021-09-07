package com.copetti.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class KafkaRecordCreationException extends RuntimeException {

    public KafkaRecordCreationException(final String message, final JsonProcessingException e) {
        super(message, e);
    }

}
