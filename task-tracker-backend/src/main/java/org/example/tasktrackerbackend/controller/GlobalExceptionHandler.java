package org.example.tasktrackerbackend.controller;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbackend.exception.*;
import org.example.tasktrackerbackend.model.dto.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> handleUserNotFoundException(UserNotFoundException e) {
        return createErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseError> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return createErrorResponse(e, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ResponseError responseError = new ResponseError();
        responseError.setStatus(HttpStatus.BAD_REQUEST.value());
        responseError.setMessage("Validation failed");
        responseError.setError(errors.toString());
        responseError.setTimestamp(Instant.now().toString());

        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolationException(ConstraintViolationException e) {
        ResponseError responseError = new ResponseError();
        responseError.setStatus(HttpStatus.BAD_REQUEST.value());
        responseError.setMessage("Validation failed");
        responseError.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        responseError.setTimestamp(Instant.now().toString());

        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseError> handleBadCredentialsException(BadCredentialsException e) {
        return createErrorResponse(e, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseError> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return createErrorResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleException(Exception e) {
        ResponseError responseError = new ResponseError();
        responseError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseError.setMessage("unknown error");
        responseError.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        responseError.setTimestamp(Instant.now().toString());
        return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
    * Одна ошибка идет в JwtFilter
    *
    * */

    private ResponseEntity<ResponseError> createErrorResponse(Exception e, HttpStatus status) {
        ResponseError responseError = new ResponseError();
        responseError.setStatus(status.value());
        responseError.setMessage(e.getMessage());
        responseError.setError(status.getReasonPhrase());
        responseError.setTimestamp(Instant.now().toString());
        return new ResponseEntity<>(responseError, status);
    }
}