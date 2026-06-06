package com.avijitnath.url_shortener.exception;

import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UrlNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(exc.getMessage());
        error.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UrlExpiredException.class})
    public ResponseEntity<ErrorResponse> handleUrlExpiredException(UrlExpiredException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.GONE.value());
        error.setErrorMessage(exc.getMessage());
        error.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.GONE);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(exc.getMessage());
        error.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleDefaultException(Exception exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setErrorMessage(exc.getMessage());
        error.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
