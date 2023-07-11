package com.gwsoft.restaurantAPI.model;

public class CuisineDTO {
    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    private String cuisine; // we want to set this as primary key to do pagination
}
