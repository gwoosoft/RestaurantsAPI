package com.gwsoft.restaurantAPI.advice;

import com.gwsoft.restaurantAPI.error.CuisineNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestaurantAPIApplicationExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CuisineNotFoundException.class)
    public Map<String, String> handleBusinessException(CuisineNotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> handleBusinessException(RuntimeException ex) {
        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", ex.getMessage()); we should probably do not want to give actual backend error details to client
        errorMap.put("errorMessage", "it is unknown exception, please contact to server admin");
        return errorMap;
    }
}
