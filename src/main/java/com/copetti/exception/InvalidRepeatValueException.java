package com.copetti.exception;

import com.copetti.core.KafkaRestService;

public class InvalidRepeatValueException extends Exception {

    public InvalidRepeatValueException(String repeatValue) {
        super("'" + repeatValue + "' is not a valid value for " + KafkaRestService.REPEAT_PUBLISH_TAG);
    }
}
