package com.gwsoft.restaurantAPI.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.temporal.ChronoUnit;

@DynamoDBTable(tableName="nyc-restaurants")
public class RestaurantEntity {
    private String cuisine; // we want to set this as primary key to do pagination
    private String name;
    private String id;
    private Integer reviewCount;
    private String rating;
    private String address1;

    private String zipCode;
    private String phone;

    private Integer cuisineGlobal; // to query all the cuisines in the table

    private String customRating;

    @DynamoDBHashKey(attributeName="cuisine") // This should be partition key
    public String getCuisine() {
        return cuisine;
    }
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    @DynamoDBRangeKey(attributeName="name") // sort key
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName="Id")
    public String getId() { return id; }
    public void setId(String id) {this.id = id; }

    @DynamoDBAttribute(attributeName="review")
    public Integer getReviewCount() {
        return reviewCount;
    }
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    @DynamoDBIndexRangeKey(attributeName="rating", localSecondaryIndexName = "rating-index")
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }

    @DynamoDBAttribute(attributeName="address1")
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @DynamoDBAttribute(attributeName="zipcode")
    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @DynamoDBAttribute(attributeName="phone")
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "cuisine-global-cuisine-index")
    @DynamoDBAttribute(attributeName="cuisine-global")
    public Integer getCuisineGlobal() {
        return cuisineGlobal;
    }
    public void setCuisineGlobal(Integer cuisineGlobal) {
        this.cuisineGlobal = cuisineGlobal;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "custom-rating-index")
    @DynamoDBAttribute(attributeName="custom-rating")
    public void setCustomRating(String customRating) {
        this.customRating = customRating;
    }

    public RestaurantDTO asRestaurantDTO(){
        RestaurantDTO dto = new RestaurantDTO();
        dto.setCuisine(this.cuisine);
        dto.setAddress1(this.address1);
        dto.setId(this.id);
        dto.setPhone(this.phone);
        dto.setRating(this.rating);
        dto.setReviewCount(this.reviewCount);
        dto.setName(this.name);
        dto.setCuisineGlobal(this.cuisineGlobal);
        return dto;
    }

    public CuisineDTO asCuisineDTO(){
        CuisineDTO dto = new CuisineDTO();
        dto.setCuisine(this.cuisine);
        return dto;
    }
}
