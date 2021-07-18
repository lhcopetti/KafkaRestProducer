package com.copetti.consumer;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class KafkaMessage {
    private String value;
}
