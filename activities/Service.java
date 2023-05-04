package org.example.activities;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.example.model.RestaurantEntity;
import org.example.repository.DynamoDBRepository;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Service {
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
    private final DynamoDBRepository dynamoDBRepository = new DynamoDBRepository();
    private final APIGWResponse apigwResponse = new APIGWResponse();
    private final JSONObject responseBody = new JSONObject();

    private final Gson gson = new Gson();

    private Integer statusCode;

    public Service() {
        this.statusCode = 200;
    }

    public APIGatewayProxyResponseEvent getTopRestaurantsBasedOnRating(String rating){
        try{
            ScanResultPage<RestaurantEntity> results = dynamoDBRepository.getTopRestaurantsBasedOnRating(rating, this.dynamoDBMapper);

            ArrayList<Map<String, String>> cuisineList = (ArrayList<Map<String, String>>) results.getResults().stream().map((result)->{
                Map<String, String> obj = new HashMap<>();
                obj.put("cuisine", result.getCuisine());
                obj.put("rating", result.getRating().toString());
                obj.put("restaurant", result.getName());
                return obj;
            }).collect(Collectors.toList());

            responseBody.put("item", gson.toJson(cuisineList));
            responseBody.put("lastEvaluatedKey", results.getLastEvaluatedKey());
        }catch(Exception e){
            System.out.println("Error querying all cuisines available in NYC");
            this.statusCode=400;
            responseBody.put("message", "No cuisines found in DB");
        }
        return apigwResponse.handleResponse(this.statusCode, responseBody);
    };

    public APIGatewayProxyResponseEvent getAllCuisines(){
        try{
            QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getAllCuisines(this.dynamoDBMapper);
            ArrayList<String> cuisineList = (ArrayList<String>) results.getResults().stream().map((result)->{
                return result.getCuisine();
            }).collect(Collectors.toList());

            responseBody.put("item", gson.toJson(cuisineList));
            responseBody.put("lastEvaluatedKey", results.getLastEvaluatedKey());
        }catch(Exception e){
            System.out.println("Error querying all cuisines available in NYC");
            this.statusCode=400;
            responseBody.put("message", "No cuisines found in DB");
        }
        return apigwResponse.handleResponse(this.statusCode, responseBody);
    };

}
