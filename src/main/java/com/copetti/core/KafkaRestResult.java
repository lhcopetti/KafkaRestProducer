package com.copetti.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KafkaRestResult {

    private final int times;
    private final KafkaRestRequest request;

}
