package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gwsoft.restaurantAPI.model.CuisineDTO;
import com.gwsoft.restaurantAPI.model.RestaurantDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantServiceHelper {
    private final Gson gsonHelper = new Gson();
    public ArrayList getRestaurantDtoList(QueryResultPage<RestaurantEntity> results){
        ArrayList<RestaurantDTO> restaurantDtoList = new ArrayList<RestaurantDTO>();
        var restaurantIterator = results.getResults().iterator();
        while(restaurantIterator.hasNext()){
            restaurantDtoList.add(restaurantIterator.next().asRestaurantDTO());
        }
        return restaurantDtoList;
    }

    public ArrayList getCuisineDtoList(QueryResultPage<RestaurantEntity> results){
        ArrayList<CuisineDTO> cuisineDtoList = new ArrayList<CuisineDTO>();
        var restaurantIterator = results.getResults().iterator();
        while(restaurantIterator.hasNext()){
            cuisineDtoList.add(restaurantIterator.next().asCuisineDTO());
        }
        return cuisineDtoList;
    }

    public Map<String, AttributeValue> getStartToken(String lastEvaluatedKey){
        String decode64Value=null;

        if(lastEvaluatedKey!=null){
            decode64Value= new String(Base64.decodeBase64(lastEvaluatedKey), Charset.forName("UTF-8"));
        }
        Map<String, AttributeValue> startToken = gsonHelper.fromJson(decode64Value, new TypeToken<HashMap<String, AttributeValue>>(){}.getType());
        return startToken;
    }

    public String getEncodedLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKey) throws UnsupportedEncodingException {
        return Base64.encodeBase64String(gsonHelper.toJson(lastEvaluatedKey).getBytes("UTF-8"));
    }
}
