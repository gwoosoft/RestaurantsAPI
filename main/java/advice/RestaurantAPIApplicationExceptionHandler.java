package com.gwsoft.restaurantAPI.advice;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.gwsoft.restaurantAPI.error.CuisineNotFoundException;
import com.gwsoft.restaurantAPI.error.RestaurantAPIErrorException;
import com.gwsoft.restaurantAPI.error.TokenMalformedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;

import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestaurantAPIApplicationExceptionHandler {

    private final String errorMessage = "errorMessage";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonSyntaxException.class)
    public Map<String, String> handleInvalidArgument(JsonSyntaxException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<String, String> handleInvalidArgument(BindException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public Map<String, String> handleInvalidArgument(NumberFormatException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CuisineNotFoundException.class)
    public Map<String, String> handleBusinessException(CuisineNotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> handleBusinessException(RuntimeException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, "it is unknown exception, please contact to server admin");
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnsupportedEncodingException.class)
    public Map<String, String> handleBusinessException(UnsupportedEncodingException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getLocalizedMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MalformedJsonException.class)
    public Map<String, String> handleBusinessException(MalformedJsonException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getLocalizedMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenMalformedException.class)
    public Map<String, String> handleBusinessException(TokenMalformedException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RestaurantAPIErrorException.class)
    public Map<String, String> handleBusinessException(RestaurantAPIErrorException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(errorMessage, ex.getMessage());
        return errorMap;
    }
}
