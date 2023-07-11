package com.gwsoft.restaurantAPI.error;

import org.springframework.http.HttpStatus;

public class RestaurantAPIErrorException extends RuntimeException{

    public RestaurantAPIErrorException() {
        super();
    }

    public RestaurantAPIErrorException(HttpStatus status, Object data) {
        this.status = status;
        this.data = data;
    }

    public RestaurantAPIErrorException(String message, HttpStatus status, Object data) {
        super(message);
        this.status = status;
        this.data = data;
    }

    public RestaurantAPIErrorException(String message, Throwable cause, HttpStatus status, Object data) {
        super(message, cause);
        this.status = status;
        this.data = data;
    }

    public RestaurantAPIErrorException(Throwable cause, HttpStatus status, Object data) {
        super(cause);
        this.status = status;
        this.data = data;
    }

    private HttpStatus status = null;
    private Object data = null;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
