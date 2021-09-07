package com.copetti.controller;

import com.copetti.core.KafkaRestRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface KafkaRestRequestMapper {

    @Mapping(source = "dto.topic", target = "topicName")
    KafkaRestRequest fromDTO(KafkaRestRequestDTO dto, String brokerList);

}
