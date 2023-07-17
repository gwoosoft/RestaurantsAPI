package com.gwsoft.restaurantAPI;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.gwsoft.restaurantAPI.activity.RestaurantService;
import com.gwsoft.restaurantAPI.advice.RestaurantAPIApplicationExceptionHandler;
import com.gwsoft.restaurantAPI.error.CuisineNotFoundException;
import com.gwsoft.restaurantAPI.error.TokenMalformedException;
import com.gwsoft.restaurantAPI.model.*;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import javax.xml.bind.ValidationException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@WebMvcTest(RestaurantAPIApplicationController.class)
public class RestaurantAPIApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private RestaurantAPIApplicationExceptionHandler restaurantAPIApplicationExceptionHandler;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private DynamoDBRepository dynamoDBRepository;
    @InjectMocks
    private RestaurantAPIApplicationController restaurantAPIApplicationController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);


    }

    @Test
    public void getTopRestaurantsBasedOnRatingByCuisine() throws MalformedJsonException, UnsupportedEncodingException, TokenMalformedException, CuisineNotFoundException {
        QueryResultPage<RestaurantEntity> queryResultPage = new QueryResultPage<>();
        List<RestaurantEntity> results = createMockQueryResultPage();
        queryResultPage.setResults(results);
        when(dynamoDBRepository.getAllCuisines(null, 1)).thenReturn(queryResultPage);

        GetTopRestaurantsBasedOnRatingByCuisineRequest getTopRestaurantsBasedOnRatingByCuisineRequest;
        getTopRestaurantsBasedOnRatingByCuisineRequest = new GetTopRestaurantsBasedOnRatingByCuisineRequest();
        getTopRestaurantsBasedOnRatingByCuisineRequest.setCuisine("korean");
        getTopRestaurantsBasedOnRatingByCuisineRequest.setRating(3.0);
        getTopRestaurantsBasedOnRatingByCuisineRequest.setMaxNum(1);

        Integer maxNum = getTopRestaurantsBasedOnRatingByCuisineRequest.getMaxNum();
        String lastEvaluatedKey = getTopRestaurantsBasedOnRatingByCuisineRequest.getLastEvaluatedKey();
        double rating = getTopRestaurantsBasedOnRatingByCuisineRequest.getRating();
        String cuisine = getTopRestaurantsBasedOnRatingByCuisineRequest.getCuisine();

        //set expectation
        PaginatedDTO mockResult = createMockPaginatedDTO();
        var expectation = ResponseEntity.ok(mockResult);

        when(restaurantService.ListRestaurantsBasedOnRatingByCuisine(cuisine, maxNum, lastEvaluatedKey, rating)).thenReturn(mockResult);

        //set result
        ResponseEntity<PaginatedDTO> result = restaurantAPIApplicationController.getTopRestaurantsBasedOnRatingByCuisine(getTopRestaurantsBasedOnRatingByCuisineRequest);


        var actual = new Gson().toJson(result);
        var expect = new Gson().toJson(expectation);
        assertEquals(expect, actual.toString());
    }

    @Test
    public void getTopRestaurantsBasedOnRatingByCuisineValidationExceptionWithCuisineAndRating() throws Exception {

        String exceptionParam = "cuisine=koreaN&rating=6";
        String expected = "{\"statusCode\":400,\"message\":\"{rating=must be less than or equal to 5, cuisine=Make sure the cuisine is lower case}\",\"description\":\"uri=/getTopRestaurantsBasedOnRatingByCuisine\"}";
        mvc.perform(get("/getTopRestaurantsBasedOnRatingByCuisine?" + exceptionParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BindException))
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void getTopRestaurantsBasedOnRatingByCuisineValidationExceptionNullParams() throws Exception {

        String exceptionParam = "";
        String expected = "{\"statusCode\":400,\"message\":\"{rating=the value cannot be empty, cuisine=cuisine name should not be null}\",\"description\":\"uri=/getTopRestaurantsBasedOnRatingByCuisine\"}";

        mvc.perform(get("/getTopRestaurantsBasedOnRatingByCuisine?" + exceptionParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BindException))
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void getTopRestaurantsBasedOnRatingByCuisineValidationExceptionMaxNum() throws Exception {

        String exceptionParam = "cuisine=korea&rating=4&maxNum=1111";
        String expected = "{\"statusCode\":400,\"message\":\"{maxNum=please request only digits between 0 and 999}\",\"description\":\"uri=/getTopRestaurantsBasedOnRatingByCuisine\"}";

        mvc.perform(get("/getTopRestaurantsBasedOnRatingByCuisine?" + exceptionParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BindException))
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    public List createMockQueryResultPage() {
        RestaurantEntity val = new RestaurantEntity();
        val.setCuisine("korean");
        val.setName("Jongro BBQ");
        val.setId("FZpm4_fbd6P984foOUejWg");
        val.setReviewCount(2468);
        val.setRating(4.0);
        val.setAddress1("22 W 32nd St");
        val.setPhone("+12124732233");
        List<RestaurantEntity> results = new ArrayList<>();
        results.add(val);
        return results;
    }

    public PaginatedDTO createMockPaginatedDTO() {

        final String lastToken = "eyJyYXRpbmciOnsibiI6IjQifSwibmFtZSI6eyJzIjoiSm9uZ3JvIEJCUSJ9LCJjdWlzaW5lIjp7InMiOiJrb3JlYW4ifX0\u003d";
        var val = new RestaurantDTO();

        val.setCuisine("korean");
        val.setName("Jongro BBQ");
        val.setId("FZpm4_fbd6P984foOUejWg");
        val.setReviewCount(2468);
        val.setRating(4.0);
        val.setAddress1("22 W 32nd St");
        val.setPhone("+12124732233");

        var expectedValue = new ArrayList<RestaurantDTO>(
                Arrays.asList(val)
        );

        return PaginatedDTO.builder().items(Collections.singletonList(expectedValue)).lastTokens(lastToken).build();
    }
}
