package org.example.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName="nyc-restaurants")
public class RestaurantEntity {
    private String cuisine; // we want to set this as primary key to do pagination
    private String name;
    private String id;
    private Integer reviewCount;
    private Double rating;
    private String address1;

    private String zipCode;
    private String phone;

    private Integer cuisineGlobal; // to query all the cuisines in the table

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
    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
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
}
