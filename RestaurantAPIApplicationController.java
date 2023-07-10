package com.gwsoft.restaurantAPI;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gwsoft.restaurantAPI.activity.RestaurantService;
import com.gwsoft.restaurantAPI.model.PaginatedDTO;
import com.gwsoft.restaurantAPI.model.RestaurantDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RestaurantAPIApplicationController {
    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDBRepository dynamoDBRepository;
    private final Gson gsonHelper = new Gson();
    private final Integer MAX_NUM = 1000;

    private final RestaurantService restaurantService;

    private static final Logger LOG = LogManager.getLogger(RestaurantAPIApplicationController.class);

    public RestaurantAPIApplicationController() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        this.dynamoDBMapper = new DynamoDBMapper(client);
        this.dynamoDBRepository= new DynamoDBRepository();
        this.restaurantService = new RestaurantService(dynamoDBRepository);
    }

    @GetMapping("/getAllCuisines")
    public ResponseEntity<PaginatedDTO> getAllCuisines(@RequestParam(required = false, name="maxNum") Integer maxNum, @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey) throws JsonProcessingException {
        LOG.info("about to get all the cuisines available in nyc..");
        try {
            PaginatedDTO restaurantList = restaurantService.ListCuisines(maxNum, lastEvaluatedKey);
            return ResponseEntity.ok(restaurantList);
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getTopRestaurantsBasedOnRating")
    public ResponseEntity<PaginatedDTO> getTopRestaurantsBasedOnRating(@RequestParam(required = false, name="rating") String rating, @RequestParam(required = false, name="maxNum") Integer maxNum, @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey) throws UnsupportedEncodingException {
        try {
            PaginatedDTO restaurantList = restaurantService.ListRestaurantsBasedOnRating(maxNum, lastEvaluatedKey, rating);
            return ResponseEntity.ok(restaurantList);
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getTopRestaurantsBasedOnRatingByCuisine")
    public ResponseEntity<PaginatedDTO> getTopRestaurantsBasedOnRatingByCuisine(@RequestParam(required = false, name="rating") String cuisine, @RequestParam(required = false, name="rating") String rating, @RequestParam(required = false, name="maxNum") Integer maxNum, @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey) throws UnsupportedEncodingException {
        try {
            PaginatedDTO restaurantList = restaurantService.ListRestaurantsBasedOnRatingByCuisine(cuisine, maxNum, lastEvaluatedKey, rating);
            return ResponseEntity.ok(restaurantList);
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }
}
