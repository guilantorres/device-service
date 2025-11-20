package com.github.guilantorres.device.exceptions;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DeviceNotFoundException.class)
  ProblemDetail handleDeviceNotFound(DeviceNotFoundException exception) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
        exception.getMessage());
    problemDetail.setTitle("Device not found");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(DeviceInUseException.class)
  ProblemDetail handleDeviceInUse(DeviceInUseException exception) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
        exception.getMessage());
    problemDetail.setTitle("Device in use");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handleInvalidArgument(MethodArgumentNotValidException exception) {
    List<FieldError> errors = exception.getBindingResult().getFieldErrors();
    List<String> errorList = errors.stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "Validation failed for one or more fields");

    problemDetail.setTitle("Invalid request content");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("errors", errorList);
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail handleGlobalException(Exception exception) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error ocurred");
    problemDetail.setTitle("Internal server error");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }
}