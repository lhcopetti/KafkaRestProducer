package com.copetti.producer;

import com.copetti.service.KafkaProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "v1/publish")
@Slf4j
@RequiredArgsConstructor
public class RestProducerController {

    private final KafkaProducerService producer;

    @PostMapping("/{destination-topic}")
    public void publish(
        @PathVariable("destination-topic") String topic,
        @RequestHeader("X-KafkaRest-BrokerList") String brokerList
                       ) throws ExecutionException, JsonProcessingException, InterruptedException, TimeoutException {
        log.info("Publishing to {}", topic);
        producer.publish(brokerList, topic);
    }
}
