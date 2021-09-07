package com.copetti.controller;

import com.copetti.core.KafkaRestService;
import com.copetti.exception.InvalidRepeatValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "v1/publish")
@Slf4j
@RequiredArgsConstructor
public class RestProducerController {

    static final String HEADER_BROKER_LIST = "X-KafkaRest-BrokerList";

    private final KafkaRestService service;
    private final KafkaRestRequestMapper mapper;

    @PostMapping
    public void publish(
        @RequestHeader(HEADER_BROKER_LIST) String brokerList,
        @Valid @RequestBody KafkaRestRequestDTO request
                       ) throws InvalidRepeatValueException {
        log.info("Publishing message to brokerList: {}", brokerList);
        log.info("Public Request: {}", request);

        val kafkaRequest = mapper.fromDTO(request, brokerList);
        service.publish(kafkaRequest);
        log.info("Request published successfully to '{}'", request.getTopic());
    }

}
