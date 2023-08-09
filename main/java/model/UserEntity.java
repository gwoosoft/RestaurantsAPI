package com.gwsoft.restaurantAPI.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Map;

@DynamoDBTable(tableName = "restaurant-users")
public class UserEntity {
    /**
     * This is primary key
     */
    private String userId;

    /**
     * Attribute for map of restaurant Id to rating of the user
     */
    private Map<String, String> userSelection;

    @DynamoDBHashKey(attributeName="userId")
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName="user-selection")
    public Map<String, String> getUserSelection() {
        return userSelection;
    }
    public void setUserSelection(Map<String, String> userSelection) {
        this.userSelection = userSelection;
    }

}
