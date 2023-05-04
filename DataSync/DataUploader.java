package org.example.utills;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.google.gson.Gson;
import org.example.model.Business;
import org.example.model.Yelp;
import org.example.repository.DynamoDBRestaurantRepository;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataUploader {
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
    private final Gson gson = new Gson();
    Logger logger = Logger.getLogger(DataUploader.class.getName());

    private boolean shouldInsert(String cuisine, String id, String name, Double rating){
        DynamoDBRestaurantRepository item = new DynamoDBRestaurantRepository();
        item.setCuisine(cuisine);
        item.setId(id);
        item.setName(name);
        item.setRating(rating);
        DynamoDBRestaurantRepository result = dynamoDBMapper.load(item);
        return result == null;
    }

    private boolean isGlobalCuisineExist(String cuisine){
        DynamoDBRestaurantRepository restaurantRepository = new DynamoDBRestaurantRepository();
        restaurantRepository.setCuisineGlobal(1);
        restaurantRepository.setCuisine(cuisine);
        System.out.println("query DB right now...");
        DynamoDBQueryExpression<DynamoDBRestaurantRepository> queryExpression =  new DynamoDBQueryExpression<DynamoDBRestaurantRepository>()
                .withHashKeyValues(restaurantRepository)
                .withIndexName("cuisine-global-index")
                .withConsistentRead(false);
        
        try{
           return dynamoDBMapper.query(DynamoDBRestaurantRepository.class, queryExpression);
        }catch(Exception e){
            System.out.println("Global index for" + cuisine + " looks like it is not existing in DB");
            return null;
        }
    }

    public void writeDB(String response, String cuisine){
        Integer cuisineFlag=0;

        Yelp yelp = gson.fromJson(response, Yelp.class);
        ArrayList<Business> businesses=yelp.getBusinesses();

        try {
            for(Business obj:businesses){
                DynamoDBRestaurantRepository item = new DynamoDBRestaurantRepository();
                item.setCuisine(cuisine);
                item.setName(obj.getName());
                item.setId(obj.getId());
                if(cuisineFlag == 0){
                    if(isGlobalCuisineExist(cuisine) == true){
                        System.out.println("This is probably first time checking this cuisine and this cuisine has never been registered globally");
                        cuisineFlag=1;
                        item.setCuisineGlobal(1);
                    }else{
                        System.out.println("This is probably first time checking this cuisine, but cuisine global pre-exist already");
                    }
                }
                if(!shouldInsert(cuisine, obj.getId(),obj.getName(), obj.getRating())){
                    System.out.println("no need to put this item as it exists in DB already..");
                    continue;
                }
                item.setAddress1(obj.getLocation().getAddress1());
                item.setZipCode(obj.getLocation().getZip_code());
                item.setReviewCount(obj.getReview_count());
                item.setRating(obj.getRating());
                item.setPhone(obj.getPhone());
                dynamoDBMapper.save(item);
            }
        }
        catch(Exception e) {
            System.out.println("failed in DB, error:" + e);
        }
    }
}
