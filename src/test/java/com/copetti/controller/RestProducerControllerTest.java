package com.copetti.controller;

import com.copetti.config.MapperConfiguration;
import com.copetti.core.KafkaRestRequest;
import com.copetti.core.KafkaRestService;
import com.copetti.exception.InvalidRepeatValueException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static com.copetti.controller.RestProducerController.HEADER_BROKER_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest
@Import(MapperConfiguration.class)
class RestProducerControllerTest {

    @MockBean
    KafkaRestService service;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Captor
    ArgumentCaptor<KafkaRestRequest> captor;

    @Test
    void givenRequestWithCorrectParameters_ExpectUseCaseToBeCalled() throws Exception {
        var path = "/v1/publish";
        val body = KafkaRestRequestDTO.builder()
            .key("the-key")
            .topic("the-topic")
            .value("the-value")
            .headers(Map.of("header-key", "header-value"))
            .build();
        mvc.perform(MockMvcRequestBuilders
            .post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body))
            .header(HEADER_BROKER_LIST, "the-broker-list")
                   )
            .andExpect(status().isOk());

        verify(service).publish(captor.capture());

        KafkaRestRequest value = captor.getValue();
        assertThat(value)
            .hasFieldOrPropertyWithValue("brokerList", "the-broker-list")
            .hasFieldOrPropertyWithValue("key", "the-key")
            .hasFieldOrPropertyWithValue("value", "the-value")
            .hasFieldOrPropertyWithValue("headers.header-key", "header-value")
            .hasFieldOrPropertyWithValue("topicName", "the-topic");
    }

    @Test
    void givenRequestWithoutRequiredBrokerListHeader_expectBadRequestAndExplicitErrorMessage() throws Exception {
        var path = "/v1/publish";
        mvc.perform(MockMvcRequestBuilders.post(path))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.httpStatus").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.path").value(path))
            .andExpect(jsonPath("$.errorMessage").value(Matchers.containsString("Required request header 'X-KafkaRest-BrokerList")));

        verify(service, never()).publish(any());
    }

    @Test
    void givenRequestWithoutTopic_expectBadRequestAndExplicitErrorMessage() throws Exception {
        var headers = new HttpHeaders();
        var content = new ObjectMapper().writeValueAsString(new KafkaRestRequestDTO());
        headers.add(HEADER_BROKER_LIST, "any-broker-list");
        var post = MockMvcRequestBuilders.post("/v1/publish")
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

        mvc.perform(post)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").value(Matchers.containsString("'topic'")));

        verify(service, never()).publish(any());
    }

    @Test
    void givenRequestWithInvalidRepeatHeader_ExpectBadRequest() throws Exception {
        var invalidHeader = Map.of(KafkaRestService.REPEAT_PUBLISH_TAG, "invalid-repeat");
        var publishRequest = new KafkaRestRequestDTO(null, "the-topic", invalidHeader, "the-value");
        var content = new ObjectMapper().writeValueAsString(publishRequest);
        var post = MockMvcRequestBuilders.post("/v1/publish")
            .headers(getBrokerListHeaders())
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

        doThrow(InvalidRepeatValueException.class).when(service).publish(any());

        mvc.perform(post).andExpect(status().isBadRequest());
    }

    private HttpHeaders getBrokerListHeaders() {
        var headers = new HttpHeaders();
        headers.add(HEADER_BROKER_LIST, "any-broker-list");
        return headers;
    }

}