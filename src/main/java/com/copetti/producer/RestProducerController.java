package com.copetti.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/publish")
@Slf4j
public class RestProducerController {

    @PostMapping
    public void empty() {
        log.info("Publishing to EMPTY");
    }
    @PostMapping("/{destination-topic}")
    public void publish(@PathVariable("destination-topic") String topic) {
        log.info("Publishing to {}", topic);
    }
}
