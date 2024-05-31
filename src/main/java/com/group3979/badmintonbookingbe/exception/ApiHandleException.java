package com.group3979.badmintonbookingbe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class ApiHandleException {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleInvalidAccountException(BadCredentialsException ex) {
        return new ResponseEntity<>("Phone or password is not correct", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> handleDuplicatePhoneException(SQLIntegrityConstraintViolationException ex) {
        return new ResponseEntity<>("Duplicate phone number!!!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleException(AuthException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
