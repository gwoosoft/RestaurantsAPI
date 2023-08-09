package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;
import com.gwsoft.restaurantAPI.error.BullShitException;
import com.gwsoft.restaurantAPI.error.RestaurantAPIErrorException;
import com.gwsoft.restaurantAPI.model.CuisineDTO;
import com.gwsoft.restaurantAPI.model.PaginatedDTO;
import com.gwsoft.restaurantAPI.model.RestaurantDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * @param cuisine
     * @param maxNum
     * @param lastEvaluatedKey
     * @param rating
     * @return
     */
    public PaginatedDTO ListRestaurantsBasedOnRatingByCuisine(String cuisine, Integer maxNum, String lastEvaluatedKey, Double rating) throws MalformedJsonException, UnsupportedEncodingException {

        if (maxNum == null) {
            maxNum = MAX_NUM;
        }

        Map<String, AttributeValue> startToken = restaurantServiceHelper.getStartToken(lastEvaluatedKey);
        QueryResultPage<RestaurantEntity> results = dynamoDBRepository.getCuisineBasedOnRating(cuisine, rating, startToken, maxNum);

        LOG.debug(results.toString());

        ArrayList<RestaurantDTO> restaurantDtoList = restaurantServiceHelper.getRestaurantDtoList(results);
        LOG.debug("in RService restaurantDtoList:" + gsonHelper.toJson(restaurantDtoList));
        System.out.println("in RService restaurantDtoList:" + gsonHelper.toJson(restaurantDtoList));
        Map<String, AttributeValue> resultsLastEvaluatedKey = results.getLastEvaluatedKey();
        String lastToken = restaurantServiceHelper.getEncodedLastEvaluatedKey(resultsLastEvaluatedKey);

        try{
            return PaginatedDTO.builder().items(Collections.singletonList(restaurantDtoList)).lastTokens(lastToken).build();
        }catch (Exception e){
            throw new RestaurantAPIErrorException(
                    "Trouble getting paginated results",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    /**
     * @param maxNum
     * @param lastEvaluatedKey
     * @param rating
     * @return
     */
    public PaginatedDTO ListRestaurantsBasedOnRating(Integer maxNum, String lastEvaluatedKey, Double rating) throws UnsupportedEncodingException, MalformedJsonException {

        if (maxNum == null) {
            maxNum = MAX_NUM;
        }

        Map<String, AttributeValue> startToken = restaurantServiceHelper.getStartToken(lastEvaluatedKey);
        ScanResultPage<RestaurantEntity> results = dynamoDBRepository.getTopRestaurantsBasedOnRating(rating, startToken, maxNum);

        var restaurantIterator = results.getResults().iterator();
        ArrayList<RestaurantDTO> restaurantDtoList = new ArrayList<RestaurantDTO>();

        while (restaurantIterator.hasNext()) {
            restaurantDtoList.add(restaurantIterator.next().asRestaurantDTO());
        }

        Map<String, AttributeValue> resultsLastEvaluatedKey = results.getLastEvaluatedKey();

        String lastToken = restaurantServiceHelper.getEncodedLastEvaluatedKey(resultsLastEvaluatedKey);

        try{
            return PaginatedDTO.builder().items(Collections.singletonList(restaurantDtoList)).lastTokens(lastToken).build();
        }catch (Exception e){
            throw new RestaurantAPIErrorException(
                    "Trouble getting paginated results",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    /**
     * @param maxNum
     * @param lastEvaluatedKey
     * @return
     */
    public PaginatedDTO ListCuisines(Integer maxNum, String lastEvaluatedKey) throws MalformedJsonException, UnsupportedEncodingException {

        if (maxNum == null) {
            maxNum = MAX_NUM;
        }

        Map<String, AttributeValue> startToken;
        startToken = restaurantServiceHelper.getStartToken(lastEvaluatedKey);
        QueryResultPage<RestaurantEntity> results;

        results = dynamoDBRepository.getAllCuisines(startToken, maxNum);
        ArrayList<CuisineDTO> cuisineDtoList = restaurantServiceHelper.getCuisineDtoList(results);
        Map<String, AttributeValue> resultsLastEvaluatedKey = results.getLastEvaluatedKey();
        String lastToken = restaurantServiceHelper.getEncodedLastEvaluatedKey(resultsLastEvaluatedKey);

        try{
            return PaginatedDTO.builder().items(Collections.singletonList(cuisineDtoList)).lastTokens(lastToken).build();
        }catch (Exception e){
            throw new RestaurantAPIErrorException(
                    "Trouble getting paginated results",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    /**
     *
     * @param restaurantEntity
     * @param customRating
     * @return
     */
    public RestaurantEntity updateCustomRate(RestaurantEntity restaurantEntity, String customRating, String userId){
        try{
            return dynamoDBRepository.putCustomRate(restaurantEntity, customRating, userId);
        }catch(Exception e){
            LOG.debug(e.getMessage());
            throw new RestaurantAPIErrorException(
                    "Trouble updating the item due to " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }
}
