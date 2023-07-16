package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import com.gwsoft.restaurantAPI.error.CuisineNotFoundException;
import com.gwsoft.restaurantAPI.error.RestaurantAPIErrorException;
import com.gwsoft.restaurantAPI.model.CuisineDTO;
import com.gwsoft.restaurantAPI.model.PaginatedDTO;
import com.gwsoft.restaurantAPI.model.RestaurantDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class RestaurantService {

    public DynamoDBRepository dynamoDBRepository;
    private final Integer MAX_NUM = 1000;
    private final Gson gsonHelper = new Gson();

    private final RestaurantServiceHelper restaurantServiceHelper;

    public RestaurantService() {
        this.dynamoDBRepository = new DynamoDBRepository();
        this.restaurantServiceHelper = new RestaurantServiceHelper();

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

        Map<String, AttributeValue> startToken = restaurantServiceHelper.getStartToken(lastEvaluatedKey);
        QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getCuisineBasedOnRating(cuisine, rating, startToken, maxNum);

        LOG.debug(results.toString());

        try{
            ArrayList<RestaurantDTO> restaurantDtoList = restaurantServiceHelper.getRestaurantDtoList(results);
            LOG.debug("in RService restaurantDtoList:" + gsonHelper.toJson(restaurantDtoList));
            Map<String, AttributeValue> resultsLastEvaluatedKey= results.getLastEvaluatedKey();
            String lastToken = Base64.encodeBase64String(gsonHelper.toJson(resultsLastEvaluatedKey).getBytes("UTF-8"));
            return PaginatedDTO.builder().items(Collections.singletonList(restaurantDtoList)).lastTokens(lastToken).build();
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

        Map<String, AttributeValue> startToken = restaurantServiceHelper.getStartToken(lastEvaluatedKey);
        ScanResultPage<RestaurantEntity> results = dynamoDBRepository.getTopRestaurantsBasedOnRating(rating, startToken, maxNum);

        var restaurantIterator = results.getResults().iterator();
        ArrayList<RestaurantDTO> restaurantDtoList = new ArrayList<RestaurantDTO>();

        try{
            while(restaurantIterator.hasNext()){
                restaurantDtoList.add(restaurantIterator.next().asRestaurantDTO());
            }
            Map<String, AttributeValue> resultsLastEvaluatedKey= results.getLastEvaluatedKey();
            String lastToken = Base64.encodeBase64String(gsonHelper.toJson(resultsLastEvaluatedKey).getBytes("UTF-8"));
            return PaginatedDTO.builder().items(Collections.singletonList(restaurantDtoList)).lastTokens(lastToken).build();
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
    public PaginatedDTO ListCuisines(Integer maxNum, String lastEvaluatedKey) throws UnsupportedEncodingException, CuisineNotFoundException {

        if(maxNum==null){
            maxNum=MAX_NUM;
        }

        Map<String, AttributeValue> startToken = restaurantServiceHelper.getStartToken(lastEvaluatedKey);

        QueryResultPage<RestaurantEntity> results;

        try {
            results = dynamoDBRepository.getAllCuisines(startToken, maxNum);
            ArrayList<CuisineDTO> restaurantDtoList = restaurantServiceHelper.getCuisineDtoList(results);
            Map<String, AttributeValue> resultsLastEvaluatedKey= results.getLastEvaluatedKey();
            String lastToken = Base64.encodeBase64String(gsonHelper.toJson(resultsLastEvaluatedKey).getBytes("UTF-8"));
            return PaginatedDTO.builder().items(Collections.singletonList(restaurantDtoList)).lastTokens(lastToken).build();
        }
        catch (RestaurantAPIErrorException e){
            if(e instanceof RestaurantAPIErrorException){
                throw new CuisineNotFoundException("There was an error fetching data from DB, Please contact Server manager. The error is caused by " + e.getMessage());
            }
            else{
                throw new RuntimeException("some unknown Error!!");
            }
        }
    }

}
