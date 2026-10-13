package com.nisum.bci.controller.advice;

import com.nisum.bci.constants.Constants;
import com.nisum.bci.dto.ErrorResponse;
import com.nisum.bci.exception.EmailAlreadyExistsException;
import com.nisum.bci.exception.InvalidFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {EmailAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> emailAlreadyExistsExceptionHandler(EmailAlreadyExistsException e) {
        log.warn("{}, emailAlreadyExistsException={}", Constants.LOG_EXCEPTION, e.getMessage());
        return buildResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        log.warn("{}, dataIntegrityViolationException={}", Constants.LOG_EXCEPTION,
                e.getMostSpecificCause().getMessage());
        return buildResponseEntity(Constants.MSG_EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {InvalidFieldException.class})
    public ResponseEntity<ErrorResponse> invalidFieldExceptionHandler(InvalidFieldException e) {
        log.warn("{}, invalidFieldException={}", Constants.LOG_EXCEPTION, e.getMessage());
        return buildResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .sorted()
                .collect(Collectors.joining("; "));
        log.warn("{}, methodArgumentNotValidException={}", Constants.LOG_EXCEPTION, message);
        return buildResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.warn("{}, httpMessageNotReadableException={}", Constants.LOG_EXCEPTION, e.getMessage());
        return buildResponseEntity(Constants.MSG_MALFORMED_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedExceptionHandler(
            HttpMediaTypeNotSupportedException e) {
        log.warn("{}, httpMediaTypeNotSupportedException={}", Constants.LOG_EXCEPTION, e.getMessage());
        return buildResponseEntity(Constants.MSG_UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandler(
            HttpRequestMethodNotSupportedException e) {
        log.warn("{}, httpRequestMethodNotSupportedException={}", Constants.LOG_EXCEPTION, e.getMessage());
        return buildResponseEntity(Constants.MSG_METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Order
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception e) {
        log.error("{}, exceptionMessage={}", Constants.LOG_EXCEPTION, e.getMessage(), e);
        return buildResponseEntity(Constants.MSG_INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(String message, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(message), status);
    }
}
