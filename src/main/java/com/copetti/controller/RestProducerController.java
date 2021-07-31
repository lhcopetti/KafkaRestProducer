package com.copetti.controller;

import com.copetti.service.KafkaProducerService;
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

    private final KafkaProducerService producer;

    @PostMapping
    public void publish(
        @RequestHeader("X-KafkaRest-BrokerList") String brokerList,
        @Valid @RequestBody PublishRequest request
                       ) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        log.info("Publishing message to brokerList: {}", brokerList);
        log.info("Public Request: {}", request);

        var producerRequest = KafkaProducerMapper.mapFromRequest(brokerList, request);
        producer.publish(producerRequest);
    }
}
