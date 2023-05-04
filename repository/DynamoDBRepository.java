package org.example.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import org.example.model.RestaurantEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamoDBRepository {

    public QueryResultPage<RestaurantEntity> getCuisineBasedOnRating(String cuisine, Map<String, AttributeValue> lastEvaluatedKey, Integer maxNum, DynamoDBMapper dynamoDBMapper){

        RestaurantEntity restaurants = new RestaurantEntity();
        restaurants.setCuisine(cuisine);
        Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN("4.6"));

        DynamoDBQueryExpression<RestaurantEntity> queryExpression = new DynamoDBQueryExpression<RestaurantEntity>()
                .withHashKeyValues(restaurants)
                .withRangeKeyCondition("rating",rangeKeyCondition)
                .withLimit(maxNum);

        queryExpression.setExclusiveStartKey(lastEvaluatedKey);
        QueryResultPage<RestaurantEntity> queryPage = dynamoDBMapper.queryPage(RestaurantEntity.class, queryExpression);

        return queryPage;
    }

    /**
     * this method scan DB and return restaurants based on the rate user request
     *
     * @param  value a value of the rating of the restaurant
     * @param  dynamoDBMapper
     * @return  ScanResultPage<RestaurantEntity>
     */
    public ScanResultPage<RestaurantEntity> getTopRestaurantsBasedOnRating(String value, DynamoDBMapper dynamoDBMapper) {

        HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":rating", new AttributeValue().withN(value));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("rating > :rating")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResultPage<RestaurantEntity> scanResultPage = dynamoDBMapper.scanPage(RestaurantEntity.class, scanExpression);

        return scanResultPage;
    };


    /**
     * this method query DB and return all the cuisines available in nyc
     *
     * @param  dynamoDBMapper
     * @return  QueryResultPage
     */
    public QueryResultPage<RestaurantEntity> getAllCuisines(DynamoDBMapper dynamoDBMapper) {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCuisineGlobal(1);

        DynamoDBQueryExpression<RestaurantEntity> queryExpression =  new DynamoDBQueryExpression<RestaurantEntity>()
                .withHashKeyValues(restaurantEntity)
                .withIndexName("cuisine-global-cuisine-index")
                .withConsistentRead(false);

        try{
            QueryResultPage<RestaurantEntity> queryResultPage = dynamoDBMapper.queryPage(RestaurantEntity.class, queryExpression);
            return queryResultPage;
        }catch(Exception e){
            System.out.println("Error querying all cuisines available in NYC");
        }
        return null;
    };

}