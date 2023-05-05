package com.gwsoft.restaurantAPI;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RestaurantAPIApplicationController {
    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDBRepository dynamoDBRepository;

    private static final Logger LOG = LogManager.getLogger(RestaurantAPIApplicationController.class);

    public RestaurantAPIApplicationController() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        this.dynamoDBMapper = new DynamoDBMapper(client);
        this.dynamoDBRepository= new DynamoDBRepository();
    }

    @GetMapping("/getAllCuisines")
    public ArrayList<String> getAllCuisines(){
        LOG.info("about to get all the cuisines available in nyc..");
        QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getAllCuisines(this.dynamoDBMapper);
        try {
        return (ArrayList<String>) results.getResults().stream().map(RestaurantEntity::getCuisine).collect(Collectors.toList());
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getTopRestaurantsBasedOnRating")
    public ArrayList<Map<String, String>> getTopRestaurantsBasedOnRating(String rating){
        ScanResultPage<RestaurantEntity> results = dynamoDBRepository.getTopRestaurantsBasedOnRating(rating, this.dynamoDBMapper);
       try {
           return (ArrayList<Map<String, String>>) results.getResults().stream().map((result) -> {
               Map<String, String> obj = new HashMap<>();
               obj.put("cuisine", result.getCuisine());
               obj.put("rating", result.getRating().toString());
               obj.put("restaurant", result.getName());
               return obj;
           }).collect(Collectors.toList());
       }catch (Exception e){
           LOG.info("error:"+e);
           throw new RuntimeException(e);
       }
    }
}
