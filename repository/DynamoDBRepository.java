package com.gwsoft.restaurantAPI.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;


import java.util.HashMap;
import java.util.Map;

public class DynamoDBRepository {

    /**
     *
     * @param cuisine
     * @param lastEvaluatedKey
     * @param maxNum
     * @param dynamoDBMapper
     * @return
     */
    public QueryResultPage<RestaurantEntity> getCuisineBasedOnRating(String cuisine, String rating, Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum, DynamoDBMapper dynamoDBMapper){

        RestaurantEntity restaurants = new RestaurantEntity();
        restaurants.setCuisine(cuisine);

        double doubledRating = Double.parseDouble(rating);
        String convertedRating = Double.toString(doubledRating);

        Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN(convertedRating));

        DynamoDBQueryExpression<RestaurantEntity> queryExpression = new DynamoDBQueryExpression<RestaurantEntity>()
                .withExclusiveStartKey(lastEvaluatedKey)
                .withHashKeyValues(restaurants)
                .withRangeKeyCondition("rating",rangeKeyCondition)
                .withLimit(maxNum);

        queryExpression.setExclusiveStartKey(lastEvaluatedKey);
        try{
            QueryResultPage<RestaurantEntity> queryResultPage = dynamoDBMapper.queryPage(RestaurantEntity.class, queryExpression);
            return queryResultPage;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * this method scan DB and return restaurants based on the rate user request
     *
     * @param  value a value of the rating of the restaurant
     * @param  dynamoDBMapper
     * @return  ScanResultPage<RestaurantEntity>
     */
    public ScanResultPage<RestaurantEntity> getTopRestaurantsBasedOnRating(String value, Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum, DynamoDBMapper dynamoDBMapper) {
        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":rating", new AttributeValue().withN(value));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withExclusiveStartKey(lastEvaluatedKey)
                .withLimit(maxNum)
                .withFilterExpression("rating > :rating")
                .withExpressionAttributeValues(expressionAttributeValues);
        try{
            ScanResultPage<RestaurantEntity> scanResultPage = dynamoDBMapper.scanPage(RestaurantEntity.class , scanExpression);
            return scanResultPage;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    };


    /**
     * this method query DB and return all the cuisines available in nyc
     *
     * @param  dynamoDBMapper
     * @return  QueryResultPage
     */
    public QueryResultPage<RestaurantEntity> getAllCuisines(DynamoDBMapper dynamoDBMapper, Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum) {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCuisineGlobal(1);

        DynamoDBQueryExpression<RestaurantEntity> queryExpression =  new DynamoDBQueryExpression<RestaurantEntity>()
                .withExclusiveStartKey(lastEvaluatedKey)
                .withHashKeyValues(restaurantEntity)
                .withLimit(maxNum)
                .withIndexName("cuisine-global-cuisine-index")
                .withConsistentRead(false);

        try{
            QueryResultPage<RestaurantEntity> queryResultPage = dynamoDBMapper.queryPage(RestaurantEntity.class, queryExpression);
            return queryResultPage;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    };

}
