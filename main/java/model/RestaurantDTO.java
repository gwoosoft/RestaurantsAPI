package com.gwsoft.restaurantAPI.model;

public class RestaurantDTO {
    private String cuisine; // we want to set this as primary key to do pagination
    private String name;
    private String id;
    private Integer reviewCount;
    private Double rating;
    private String address1;
    private String zipCode;
    private String phone;

    private Integer cuisineGlobal; // to query all the cuisines in the table

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCuisineGlobal() {
        return cuisineGlobal;
    }

    public void setCuisineGlobal(Integer cuisineGlobal) {
        this.cuisineGlobal = cuisineGlobal;
    }
}
