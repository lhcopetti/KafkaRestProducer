
# KafkaRestProducer

[![Java CI](https://github.com/lhcopetti/KafkaRestProducer/actions/workflows/action.yml/badge.svg)](https://github.com/lhcopetti/KafkaRestProducer/actions/workflows/action.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lhcopetti_KafkaRestProducer&metric=alert_status)](https://sonarcloud.io/dashboard?id=lhcopetti_KafkaRestProducer)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=lhcopetti_KafkaRestProducer&metric=coverage)](https://sonarcloud.io/dashboard?id=lhcopetti_KafkaRestProducer)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=lhcopetti_KafkaRestProducer&metric=ncloc)](https://sonarcloud.io/dashboard?id=lhcopetti_KafkaRestProducer)

Produce JSON messages to a Kafka Broker using a simple REST API

This project will allow you to produce a payload to a Kafka topic using a JSON body like this:
```
{
    "topic": "your-topic-name",
    "headers": {
        "uniqueId": "maybe a UUIDv4"
    },
    "value": {
        "fieldOne": "valueOne",
        "fieldTwo": "valueTwo",
        "nestedObj": {
            "nestedField": "nestedValue"
        }
    }
}
```

### User Guide

#### Local Broker

To run against a local broker, refer to the docker-compose-quick-start.yml. Copy that file to a directory and run:
```
docker compose -f docker-compose-quick-start.yml up
```

It also includes the kowl project, which allows you to visualize your cluster, topics, messages and so on (runs by default on localhost:8080)

Then run the following cURL command:

```
curl --location --request POST 'http://localhost:9091/v1/publish/' \
--header 'X-KafkaRest-BrokerList: kafka:29092' \
--header 'Content-Type: application/json' \
--data-raw '{
    "topic": "simple-topic",
    "value": {
        "field": "value"
    }
}'
```

#### Remote Broker

To produce messages on brokers that are running remotely, you can simply run a disposable container:

```
docker run --rm -d -p9091:9091 lhcopetti/kafka-rest-producer
```

Then run the same cURL:

```
curl --location --request POST 'http://localhost:9091/v1/publish/' \
--header 'X-KafkaRest-BrokerList: <YOUR_REMOTE_BROKER_LIST>' \
--header 'Content-Type: application/json' \
--data-raw '{
    "topic": "simple-topic",
    "value": {
        "field": "value"
    }
}'
```
