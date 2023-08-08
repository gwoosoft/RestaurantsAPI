package com.gwsoft.restaurantAPI;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.gwsoft.restaurantAPI.activity.RestaurantService;
import com.gwsoft.restaurantAPI.error.RestaurantAPIErrorException;
import com.gwsoft.restaurantAPI.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;

@RestController
@Validated
public class RestaurantAPIApplicationController {

    private final RestaurantService restaurantService;
    private static final Logger LOG = LogManager.getLogger(RestaurantAPIApplicationController.class);

    public RestaurantAPIApplicationController() {
        this.restaurantService = new RestaurantService();
    }

    @GetMapping("/getAllCuisines")
    public ResponseEntity<PaginatedDTO> getAllCuisines(@Valid BaseRequest baseRequest) throws UnsupportedEncodingException, MalformedJsonException, RestaurantAPIErrorException, JsonSyntaxException {
        LOG.info("about to get all the cuisines available in nyc..");

        Integer maxNum = baseRequest.getMaxNum();
        String lastEvaluatedKey = baseRequest.getLastEvaluatedKey();

        PaginatedDTO restaurantList = restaurantService.ListCuisines(maxNum, lastEvaluatedKey);
        return ResponseEntity.ok(restaurantList);
    }

    @GetMapping("/getTopRestaurantsBasedOnRating")
    public ResponseEntity<PaginatedDTO> getTopRestaurantsBasedOnRating(@Valid GetTopRestaurantsBasedOnRatingRequest getTopRestaurantsBasedOnRatingRequest) throws UnsupportedEncodingException, MalformedJsonException, RestaurantAPIErrorException, JsonSyntaxException {
        Integer maxNum = getTopRestaurantsBasedOnRatingRequest.getMaxNum();
        String lastEvaluatedKey = getTopRestaurantsBasedOnRatingRequest.getLastEvaluatedKey();
        Double rating = getTopRestaurantsBasedOnRatingRequest.getRating();
        System.out.println("show me rating:" + rating);
        PaginatedDTO restaurantList = restaurantService.ListRestaurantsBasedOnRating(maxNum, lastEvaluatedKey, rating);
        return ResponseEntity.ok(restaurantList);
    }

    @GetMapping("/getTopRestaurantsBasedOnRatingByCuisine")
    public ResponseEntity<PaginatedDTO> getTopRestaurantsBasedOnRatingByCuisine(@Valid GetTopRestaurantsBasedOnRatingByCuisineRequest getTopRestaurantsBasedOnRatingByCuisineRequest) throws UnsupportedEncodingException, MalformedJsonException, RestaurantAPIErrorException, JsonSyntaxException {

        Integer maxNum = getTopRestaurantsBasedOnRatingByCuisineRequest.getMaxNum();
        String lastEvaluatedKey = getTopRestaurantsBasedOnRatingByCuisineRequest.getLastEvaluatedKey();
        Double rating = getTopRestaurantsBasedOnRatingByCuisineRequest.getRating();
        String cuisine = getTopRestaurantsBasedOnRatingByCuisineRequest.getCuisine();
        System.out.println("show me cuisine:" + cuisine +" and the rating:" + rating + "and lasteval:" + lastEvaluatedKey);
        PaginatedDTO restaurantList = restaurantService.ListRestaurantsBasedOnRatingByCuisine(cuisine, maxNum, lastEvaluatedKey, rating);
        return ResponseEntity.ok(restaurantList);
    }

    @PutMapping("/updateRestaurantRates/{customRating}")
    public ResponseEntity<RestaurantEntity> updateRestaurantRating(@Valid UpdateRestaurantRequest updateRestaurantRequest, @RequestBody RestaurantEntity restaurantEntity){
        String customRating = String.valueOf(updateRestaurantRequest.getCustomRating());
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateCustomRate(restaurantEntity, customRating);
        System.out.println("show me updated restaurantentity"+ updatedRestaurantEntity.getCustomRating());
        return ResponseEntity.ok(updatedRestaurantEntity);
    }

}
