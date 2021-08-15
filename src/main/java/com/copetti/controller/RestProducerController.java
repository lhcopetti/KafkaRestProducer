package com.copetti.controller;

import com.copetti.core.KafkaRestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "v1/publish")
@Slf4j
@RequiredArgsConstructor
public class RestProducerController {

    static final String HEADER_BROKER_LIST = "X-KafkaRest-BrokerList";

    private final KafkaRestService service;

    @PostMapping
    public void publish(
        @RequestHeader(HEADER_BROKER_LIST) String brokerList,
        @Valid @RequestBody PublishRequest request
                       ) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        log.info("Publishing message to brokerList: {}", brokerList);
        log.info("Public Request: {}", request);

        var producerRequest = KafkaProducerMapper.mapFromRequest(brokerList, request);
        service.publish(producerRequest);
        log.info("Request published successfully to '{}'", request.getTopic());
    }
}
