package com.copetti.controller;

import com.copetti.service.KafkaProducerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class RestProducerControllerTest {

    @MockBean
    KafkaProducerService service;

    @Autowired
    RestProducerController controller;

    @Autowired
    MockMvc mvc;

    @Test
    void givenRequestWithoutRequiredBrokerListHeader_expectBadRequestAndExplicitErrorMessage() throws Exception {
        var path = "/v1/publish";
        mvc.perform(MockMvcRequestBuilders.post(path))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.httpStatus").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.path").value(path))
            .andExpect(jsonPath("$.errorMessage").value(Matchers.containsString("Required request header 'X-KafkaRest-BrokerList")));
    }
}