package com.gwsoft.restaurantAPI.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;
import com.gwsoft.restaurantAPI.activity.RestaurantServiceHelper;
import com.gwsoft.restaurantAPI.error.RestaurantAPIErrorException;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import org.springframework.http.HttpStatus;


import java.util.HashMap;
import java.util.Map;

public class DynamoDBRepository {
    public DynamoDBMapper dynamoDBMapper;
    final String GSI = "cuisine-global-cuisine-index";

    public DynamoDBRepository() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        this.dynamoDBMapper = new DynamoDBMapper(client);
    }

    /**
     * @param cuisine
     * @param lastEvaluatedKey
     * @param maxNum
     * @return
     */
    public QueryResultPage<RestaurantEntity> getCuisineBasedOnRating(String cuisine, Double rating, Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum) {

        RestaurantEntity restaurants = new RestaurantEntity();
        restaurants.setCuisine(cuisine);

        Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN(String.valueOf(rating)));

        DynamoDBQueryExpression<RestaurantEntity> queryExpression = new DynamoDBQueryExpression<RestaurantEntity>()
                .withExclusiveStartKey(lastEvaluatedKey)
                .withHashKeyValues(restaurants)
                .withRangeKeyCondition("rating", rangeKeyCondition)
                .withLimit(maxNum);

        try {
            QueryResultPage<RestaurantEntity> queryResultPage = dynamoDBMapper.queryPage(RestaurantEntity.class, queryExpression);
            return queryResultPage;
        } catch (Exception e) {
            throw new RestaurantAPIErrorException(
                    "Query error from DB for all the cuisines",
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    /**
     * this method scan DB and return restaurants based on the rate user request
     *
     * @param rating a value of the rating of the restaurant
     * @return ScanResultPage<RestaurantEntity>
     */
    public ScanResultPage<RestaurantEntity> getTopRestaurantsBasedOnRating(Double rating, Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum) {
        String value = String.valueOf(rating);
        System.out.println("show me value:" + value);
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":rating", new AttributeValue().withN(value));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withExclusiveStartKey(lastEvaluatedKey)
                .withLimit(maxNum)
                .withFilterExpression("rating > :rating")
                .withExpressionAttributeValues(expressionAttributeValues);
        try {
            ScanResultPage<RestaurantEntity> scanResultPage = dynamoDBMapper.scanPage(RestaurantEntity.class, scanExpression);
            return scanResultPage;
        } catch (Exception e) {
            throw new RestaurantAPIErrorException(
                    "Query error from DB for all the cuisines",
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    ;


    /**
     * @param lastEvaluatedKey
     * @param maxNum
     * @return QueryResultPage
     */
    public QueryResultPage<RestaurantEntity> getAllCuisines(Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum) {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCuisineGlobal(1);


        DynamoDBQueryExpression<RestaurantEntity> queryExpression = new DynamoDBQueryExpression<RestaurantEntity>()
                .withExclusiveStartKey(lastEvaluatedKey)
                .withHashKeyValues(restaurantEntity)
                .withLimit(maxNum)
                .withIndexName(GSI)
                .withConsistentRead(false);

        try {
            QueryResultPage<RestaurantEntity> queryResultPage = dynamoDBMapper.queryPage(RestaurantEntity.class, queryExpression);
            return queryResultPage;
        } catch (Exception e) {
            throw new RestaurantAPIErrorException(
                    "Query error from DB for all the cuisines",
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    ;

    public Integer getLimit(Integer maxNum) {
        return maxNum != null ? maxNum : 1000;
    }

}
