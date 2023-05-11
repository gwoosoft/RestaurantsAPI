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
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOG = LogManager.getLogger(RestaurantAPIApplicationController.class);

    public RestaurantAPIApplicationController() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        this.dynamoDBMapper = new DynamoDBMapper(client);
        this.dynamoDBRepository= new DynamoDBRepository();
    }

    @GetMapping("/getAllCuisines")
    public String getAllCuisines(@RequestParam(required = false, name="maxNum") Integer maxNum, @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey) throws JsonProcessingException {
        LOG.info("about to get all the cuisines available in nyc..");
        if(maxNum==null){
            maxNum=MAX_NUM;
        }
        String decode64Value=null;
        if(lastEvaluatedKey!=null){
            decode64Value= new String(Base64.decodeBase64(lastEvaluatedKey), Charset.forName("UTF-8"));
        }
        Map<String, AttributeValue> startToken = gsonHelper.fromJson(decode64Value, new TypeToken<HashMap<String, AttributeValue>>(){}.getType());
        QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getAllCuisines(this.dynamoDBMapper, startToken, maxNum);
        try {
            JSONObject res = new JSONObject();
            ArrayList<String> resultList = (ArrayList<String>) results.getResults().stream().map(RestaurantEntity::getCuisine).collect(Collectors.toList());
            res.put("result", resultList);
            Map<String, AttributeValue> lastToken= results.getLastEvaluatedKey();
            res.put("lastEvaluatedKey", Base64.encodeBase64String(gsonHelper.toJson(lastToken).getBytes("UTF-8")));
        return res.toString();
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getTopRestaurantsBasedOnRating")
    public String getTopRestaurantsBasedOnRating(@RequestParam(required = false, name="rating") String rating, @RequestParam(required = false, name="maxNum") Integer maxNum, @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey){
        if(maxNum==null){
            maxNum=MAX_NUM;
        }
        String decode64Value=null;
        if(lastEvaluatedKey!=null){
            decode64Value= new String(Base64.decodeBase64(lastEvaluatedKey), Charset.forName("UTF-8"));
        }
        Map<String, AttributeValue> startToken = gsonHelper.fromJson(decode64Value, new TypeToken<HashMap<String, AttributeValue>>(){}.getType());
        ScanResultPage<RestaurantEntity> results = dynamoDBRepository.getTopRestaurantsBasedOnRating(rating, startToken, maxNum, this.dynamoDBMapper);
       try {
           JSONObject res = new JSONObject();
           ArrayList<Map<String, String>> resultList = (ArrayList<Map<String, String>>) results.getResults().stream().map((result) -> {
               Map<String, String> obj = new HashMap<>();
               obj.put("cuisine", result.getCuisine());
               obj.put("rating", result.getRating().toString());
               obj.put("restaurant", result.getName());
               return obj;
           }).collect(Collectors.toList());
           res.put("result", resultList);
           Map<String, AttributeValue> lastToken= results.getLastEvaluatedKey();
           res.put("lastEvaluatedKey",  Base64.encodeBase64String(gsonHelper.toJson(lastToken).getBytes("UTF-8")));
           return res.toString();
       }catch (Exception e){
           LOG.info("error:"+e);
           throw new RuntimeException(e);
       }
    }
}
