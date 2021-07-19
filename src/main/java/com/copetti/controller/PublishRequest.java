package com.copetti.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishRequest {
    private String topic;
    private Map<String, String> headers;
    private Object value;
}
