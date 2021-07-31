package com.copetti.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    public ResponseEntity<ApiError> handleMissingRequestHeaderException(MissingRequestHeaderException ex, ServletWebRequest req) {
        var status = HttpStatus.BAD_REQUEST;
        var err = ApiError.builder()
            .error(status.getReasonPhrase())
            .httpStatus(status.value())
            .errorMessage(ex.getMessage())
            .path(req.getRequest().getRequestURI())
            .build();
        return new ResponseEntity<>(err, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
