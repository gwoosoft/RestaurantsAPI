package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.gwsoft.restaurantAPI.model.CuisineDTO;
import com.gwsoft.restaurantAPI.model.RestaurantDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;

import java.util.ArrayList;

public class RestaurantServiceHelper {
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
}
