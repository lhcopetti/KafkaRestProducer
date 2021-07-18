package com.copetti;

import com.copetti.consumer.KafkaMessage;
import com.copetti.consumer.KafkaMessageConsumer;
import com.copetti.producer.PublishRequest;
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiApplicationTest {

    private final ObjectMapper mapper = new ObjectMapper();

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
        var topicName = "simple-value-payload";
        var request = new PublishRequest();
        var payload = new SimpleValuePayloadTest("THE_EVENT_NAME", 33);
        request.setValue(payload);

        given()
            .contentType("application/json")
            .header("X-KafkaRest-BrokerList", getBrokerList())
            .body(request)
            .post(getUrl(topicName))
            .then()
            .statusCode(200);

        KafkaMessageConsumer consumer = new KafkaMessageConsumer();
        List<KafkaMessage> messages = consumer.consumeMessages(getBrokerList(), topicName);
        assertThat(messages.size()).isEqualTo(1);
        var payloadOnKafka = messages.get(0).getValue();
        var objOnKafka = mapper.readValue(payloadOnKafka, SimpleValuePayloadTest.class);
        assertThat(objOnKafka).isEqualTo(payload);
    }

    private String getUrl(String topicName) {
        return "http://localhost:" + localServerPort + "/v1/publish/" + topicName;
    }

    private String getBrokerList() {
        return kafka.getBootstrapServers();
    }

}
