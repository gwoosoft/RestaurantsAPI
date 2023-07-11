package com.gwsoft.restaurantAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.gwsoft.restaurantAPI.activity.RestaurantService;
import com.gwsoft.restaurantAPI.model.PaginatedDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
public class RestaurantAPIApplicationController {
    private final Gson gsonHelper = new Gson();
    private final Integer MAX_NUM = 1000;

    private final RestaurantService restaurantService;

    private static final Logger LOG = LogManager.getLogger(RestaurantAPIApplicationController.class);

    public RestaurantAPIApplicationController() {
        this.restaurantService = new RestaurantService();
    }

    @GetMapping("/getAllCuisines")
    public ResponseEntity<PaginatedDTO> getAllCuisines(
            @RequestParam(required = false, name="maxNum") Integer maxNum,
            @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey)
            throws JsonProcessingException {
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
    public ResponseEntity<PaginatedDTO> getTopRestaurantsBasedOnRating(
            @RequestParam(required = false, name="rating") String rating,
            @RequestParam(required = false, name="maxNum") Integer maxNum,
            @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey)
            throws UnsupportedEncodingException {
        try {
            PaginatedDTO restaurantList = restaurantService.ListRestaurantsBasedOnRating(maxNum, lastEvaluatedKey, rating);
            return ResponseEntity.ok(restaurantList);
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getTopRestaurantsBasedOnRatingByCuisine")
    public ResponseEntity<PaginatedDTO> getTopRestaurantsBasedOnRatingByCuisine(
            @RequestParam(required = false, name="cuisine") String cuisine,
            @RequestParam(required = false, name="rating") String rating,
            @RequestParam(required = false, name="maxNum") Integer maxNum,
            @RequestParam(required = false, name="lastEvaluatedKey") String lastEvaluatedKey)
            throws UnsupportedEncodingException {
        try {
            PaginatedDTO restaurantList = restaurantService.ListRestaurantsBasedOnRatingByCuisine(cuisine, maxNum, lastEvaluatedKey, rating);
            return ResponseEntity.ok(restaurantList);
        }catch (Exception e){
            LOG.info("error:"+e);
            throw new RuntimeException(e);
        }
    }
}
