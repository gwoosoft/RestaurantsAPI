package com.gwsoft.restaurantAPI.error;

public class TokenMalformedException extends Exception{
    public TokenMalformedException(String message) {
        super(message);
    }
}
