package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gwsoft.restaurantAPI.RestaurantAPIApplicationController;
import com.gwsoft.restaurantAPI.model.CuisineDTO;
import com.gwsoft.restaurantAPI.model.PaginatedDTO;
import com.gwsoft.restaurantAPI.model.RestaurantDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantService {

    private final DynamoDBRepository dynamoDBRepository;
    private final DynamoDBMapper dynamoDBMapper;
    private final Integer MAX_NUM = 1000;
    private final Gson gsonHelper = new Gson();

    public RestaurantService(DynamoDBRepository dynamoDBRepository) {
        this.dynamoDBRepository = dynamoDBRepository;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        this.dynamoDBMapper = new DynamoDBMapper(client);
    }

    private static final Logger LOG = LogManager.getLogger(RestaurantService.class);

    /**
     *
     * @param cuisine
     * @param maxNum
     * @param lastEvaluatedKey
     * @param rating
     * @return
     */
    public PaginatedDTO ListRestaurantsBasedOnRatingByCuisine(String cuisine, Integer maxNum, String lastEvaluatedKey, String rating) {

        if(maxNum==null){
            maxNum=MAX_NUM;
        }

        String decode64Value=null;

        if(lastEvaluatedKey!=null){
            decode64Value= new String(Base64.decodeBase64(lastEvaluatedKey), Charset.forName("UTF-8"));
        }

        Map<String, AttributeValue> startToken = gsonHelper.fromJson(decode64Value, new TypeToken<HashMap<String, AttributeValue>>(){}.getType());
        QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getCuisineBasedOnRating(cuisine, rating, startToken, maxNum, this.dynamoDBMapper);
        LOG.debug(results.toString());
        ArrayList<RestaurantDTO> restaurantDtoList = new ArrayList<RestaurantDTO>();
        var restaurantIterator = results.getResults().iterator();

        try{
            while(restaurantIterator.hasNext()){
                restaurantDtoList.add(restaurantIterator.next().asRestaurantDTO());
            }
            Map<String, AttributeValue> resultsLastEvaluatedKey= results.getLastEvaluatedKey();
            String lastToken = Base64.encodeBase64String(gsonHelper.toJson(resultsLastEvaluatedKey).getBytes("UTF-8"));
            PaginatedDTO paginatedDTO = new PaginatedDTO(restaurantDtoList, lastToken);
            return paginatedDTO;
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param maxNum
     * @param lastEvaluatedKey
     * @param rating
     * @return
     */
    public PaginatedDTO ListRestaurantsBasedOnRating(Integer maxNum, String lastEvaluatedKey, String rating) {

        if(maxNum==null){
            maxNum=MAX_NUM;
        }

        String decode64Value=null;
        if(lastEvaluatedKey!=null){
            decode64Value= new String(Base64.decodeBase64(lastEvaluatedKey), Charset.forName("UTF-8"));
        }

        Map<String, AttributeValue> startToken = gsonHelper.fromJson(decode64Value, new TypeToken<HashMap<String, AttributeValue>>(){}.getType());

        ScanResultPage<RestaurantEntity> results = dynamoDBRepository.getTopRestaurantsBasedOnRating(rating, startToken, maxNum, this.dynamoDBMapper);

        ArrayList<RestaurantDTO> restaurantDtoList = new ArrayList<RestaurantDTO>();

        var restaurantIterator = results.getResults().iterator();

        try{
            while(restaurantIterator.hasNext()){
                restaurantDtoList.add(restaurantIterator.next().asRestaurantDTO());
            }
            Map<String, AttributeValue> resultsLastEvaluatedKey= results.getLastEvaluatedKey();
            String lastToken = Base64.encodeBase64String(gsonHelper.toJson(resultsLastEvaluatedKey).getBytes("UTF-8"));
            PaginatedDTO paginatedDTO = new PaginatedDTO(restaurantDtoList, lastToken);
            return paginatedDTO;

        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param maxNum
     * @param lastEvaluatedKey
     * @return
     */
    public PaginatedDTO ListCuisines(Integer maxNum, String lastEvaluatedKey) {
        if(maxNum==null){
            maxNum=MAX_NUM;
        }

        String decode64Value=null;
        if(lastEvaluatedKey!=null){
            decode64Value= new String(Base64.decodeBase64(lastEvaluatedKey), Charset.forName("UTF-8"));
        }

        Map<String, AttributeValue> startToken = gsonHelper.fromJson(decode64Value, new TypeToken<HashMap<String, AttributeValue>>(){}.getType());
        QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getAllCuisines(this.dynamoDBMapper, startToken, maxNum);
        ArrayList<CuisineDTO> restaurantDtoList = new ArrayList<CuisineDTO>();

        var restaurantIterator = results.getResults().iterator();

        try{
            while(restaurantIterator.hasNext()){
                restaurantDtoList.add(restaurantIterator.next().asCuisineDTO());
            }
            Map<String, AttributeValue> resultsLastEvaluatedKey= results.getLastEvaluatedKey();
            String lastToken = Base64.encodeBase64String(gsonHelper.toJson(resultsLastEvaluatedKey).getBytes("UTF-8"));
            PaginatedDTO paginatedDTO = new PaginatedDTO(restaurantDtoList, lastToken);
            return paginatedDTO;
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

}
