package com.mymusiclist.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    String errorMessage = bindingResult.getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((error1, error2) -> error1 + ", " + error2)
        .orElse("Validation failed");

    ErrorResponse errorResponse = ErrorResponse.builder()
        .errorCode("VALIDATION_ERROR")
        .message(errorMessage)
        .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }
}
