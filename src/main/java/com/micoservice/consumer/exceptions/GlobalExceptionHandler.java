package com.micoservice.consumer.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // vai ouvir todas as exceções do tipo Exception
    public ResponseEntity<Object> handleAllException(Exception e) {
        Map<String, Object> response = new LinkedHashMap<>(); // LinkedHashMap para manter a ordem dos campos
        response.put("status code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFound.class) // vai ouvir todas as exceções do tipo ResourceNotFound
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFound e) {
        Map<String, Object> response = new LinkedHashMap<>(); // LinkedHashMap para manter a ordem dos campos
        response.put("status code", HttpStatus.NOT_FOUND.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class) // vai ouvir todas as exceções do tipo UnauthorizedException
    public ResponseEntity<Object> handleUnauthorizedExceptionException(UnauthorizedException e) {
        Map<String, Object> response = new LinkedHashMap<>(); // LinkedHashMap para manter a ordem dos campos
        response.put("status code", HttpStatus.UNAUTHORIZED.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyExists.class)
    public ResponseEntity<Object> handleAlreadyException(AlreadyExists e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status code", HttpStatus.BAD_REQUEST.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
