package com.copetti.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishRequest {
    private String key;

    @NotBlank
    private String topic;
    private Map<String, String> headers;
    private Object value;
}
