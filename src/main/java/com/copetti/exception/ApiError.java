package com.copetti.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiError {
    private int httpStatus;
    private String error;
    private String path;
    private String errorMessage;
}
