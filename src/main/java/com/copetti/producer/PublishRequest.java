package com.copetti.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishRequest {
    private Map<String, String> headers;
    private Object value;
}
