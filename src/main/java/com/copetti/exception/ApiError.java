package com.copetti.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ApiError {

    private static final HttpStatus DEFAULT_ERROR_CODE = HttpStatus.INTERNAL_SERVER_ERROR;
    @Builder.Default
    private int httpStatus = DEFAULT_ERROR_CODE.value();
    @Builder.Default
    private String error = DEFAULT_ERROR_CODE.getReasonPhrase();

    private String path;
    private String errorMessage;
}
