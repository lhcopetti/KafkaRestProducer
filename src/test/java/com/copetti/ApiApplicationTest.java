package com.copetti;

import com.copetti.consumer.KafkaMessage;
import com.copetti.consumer.KafkaMessageConsumer;
import com.copetti.controller.PublishRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiApplicationTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final KafkaMessageConsumer kafkaConsumer = new KafkaMessageConsumer();

    @LocalServerPort
    private int localServerPort;

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SimpleValuePayloadTest {

        String eventName;
        int eventId;

    }

    @Test
    public void whenValuePayload_ExpectToBeSentToKafka() throws IOException {
        var topicName = "just-payload";
        var payload = new SimpleValuePayloadTest("THE_EVENT_NAME", 33);

        makePost(new PublishRequest(topicName, null, payload));

        KafkaMessage msg = kafkaConsumer.consumeSingleMessage(getBrokerList(), topicName);
        var objOnKafka = mapper.readValue(msg.getValue(), SimpleValuePayloadTest.class);
        assertThat(objOnKafka).isEqualTo(payload);
    }

    @Test
    public void whenPayloadWithHeaders_ExpectToBeSentToKafkaWithHeaders() throws IOException {
        var topicName = "payload-with-header";
        var payload = new SimpleValuePayloadTest("THE_EVENT_NAME", 33);
        var headers = Map.of("key1", "value1", "key2", "value2");

        makePost(new PublishRequest(topicName, headers, payload));

        KafkaMessage msg = kafkaConsumer.consumeSingleMessage(getBrokerList(), topicName);

        assertThat(msg.getHeaders()).isEqualTo(headers);
        var objOnKafka = mapper.readValue(msg.getValue(), SimpleValuePayloadTest.class);
        assertThat(objOnKafka).isEqualTo(payload);
    }

    private void makePost(final PublishRequest request) {
        given()
            .contentType("application/json")
            .header("X-KafkaRest-BrokerList", getBrokerList())
            .body(request)
            .post("http://localhost:" + localServerPort + "/v1/publish/")
            .then()
            .statusCode(200);
    }

    private String getBrokerList() {
        return kafka.getBootstrapServers();
    }

}
