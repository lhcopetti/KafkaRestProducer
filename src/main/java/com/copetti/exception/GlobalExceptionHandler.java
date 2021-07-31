package com.copetti.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, ServletWebRequest req){
        var err = ApiError.builder()
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .httpStatus(HttpStatus.BAD_REQUEST.value());

        var field = ex.getFieldError();
        if (null != field)
            err.errorMessage("'" + field.getField() + "' " + field.getDefaultMessage());

        return buildResponse(err, req);
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    public ResponseEntity<ApiError> handleMissingRequestHeaderException(MissingRequestHeaderException ex, ServletWebRequest req) {
        var status = HttpStatus.BAD_REQUEST;

        var err = ApiError.builder()
            .error(status.getReasonPhrase())
            .httpStatus(status.value())
            .errorMessage(ex.getMessage());

        return buildResponse(err, req);
    }

    private static ResponseEntity<ApiError> buildResponse(ApiError.ApiErrorBuilder builder, ServletWebRequest req) {
        var apiError = builder
            .path(req.getRequest().getRequestURI())
            .build();

        var status = HttpStatus.valueOf(apiError.getHttpStatus());
        return new ResponseEntity<>(apiError, new HttpHeaders(), status);
    }
}
