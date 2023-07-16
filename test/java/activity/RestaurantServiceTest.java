package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.gwsoft.restaurantAPI.error.CuisineNotFoundException;
import com.gwsoft.restaurantAPI.error.RestaurantAPIErrorException;
import com.gwsoft.restaurantAPI.error.TokenMalformedException;
import com.gwsoft.restaurantAPI.model.CuisineDTO;
import com.gwsoft.restaurantAPI.model.PaginatedDTO;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import com.gwsoft.restaurantAPI.repository.DynamoDBRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {
    @Mock
    private DynamoDBRepository dynamoDBRepository;
    @Mock
    private RestaurantServiceHelper restaurantServiceHelper;
    private final Gson gsonHelper = new Gson();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private RestaurantService restaurantService;


    @Test
    public void ListCuisines() throws UnsupportedEncodingException, CuisineNotFoundException, TokenMalformedException, MalformedJsonException {

        QueryResultPage<RestaurantEntity> queryResultPage = new QueryResultPage<>();
        List<RestaurantEntity> results = createMockQueryResultPage();
        queryResultPage.setResults(results);

        var expectedReturnValue = createMockPaginatedDTO();

        when(dynamoDBRepository.getAllCuisines(null,1)).thenReturn(queryResultPage);

        PaginatedDTO returnValue = restaurantService.ListCuisines(1, null);

        // check if getAllCuisines function is called once
        verify(dynamoDBRepository, times(1)).getAllCuisines(null,1);

        Assert.assertNotNull(returnValue);

        var actual = gsonHelper.toJson(returnValue.getItems());
        var expected = gsonHelper.toJson(expectedReturnValue.getItems());

        System.out.println("show me actual:" + actual);
        System.out.println("show me expected:" + expected);

        Assert.assertEquals(expected, actual);

        var actualPaginatedDTO = gsonHelper.toJson(returnValue);
        var expectedPaginatedDTO = gsonHelper.toJson(expectedReturnValue);
        Assert.assertEquals(expectedPaginatedDTO, actualPaginatedDTO);

    }

    //To test if it throws RestaurantPIErrorException
    @Test
    public void ListCuisineWithRestaurantAPIError() throws UnsupportedEncodingException, TokenMalformedException, CuisineNotFoundException, MalformedJsonException {
        when(dynamoDBRepository.getAllCuisines(null,1)).thenThrow(new RestaurantAPIErrorException());

        Throwable exception = Assert.assertThrows(CuisineNotFoundException.class, () -> restaurantService.ListCuisines(1,null));

        // set Expectation
        final String expectation = "There was an error fetching data from DB, Please contact Server manager. The error is caused by null";
        Assert.assertEquals(expectation, exception.getMessage());
    }

    //To test if it throws JsonSyntaxException
    @Test
    public void ListCuisineWithJsonSyntaxExceptionException() throws UnsupportedEncodingException, TokenMalformedException, CuisineNotFoundException, MalformedJsonException {

        when(restaurantServiceHelper.getStartToken("wrongFormat")).thenThrow(new JsonSyntaxException("Error during the decoding lastEvaluated Key, please check if it is correct form"));

        Throwable exception = Assert.assertThrows(JsonSyntaxException.class, () -> restaurantService.ListCuisines(1,"wrongFormat"));

        // set Expectation
        final String expectation = "JsonSyntaxException: Error during the decoding lastEvaluated Key, please check if it is correct form";
        Assert.assertEquals(expectation, exception.getMessage());
    }

    public List createMockQueryResultPage(){
        RestaurantEntity mockData = new RestaurantEntity();
        mockData.setCuisineGlobal(1);
        mockData.setCuisine("chinese");
        List<RestaurantEntity> results = new ArrayList<>();
        results.add(mockData);
        return results;
    }

    public PaginatedDTO createMockPaginatedDTO(){
        var val = new CuisineDTO();
        val.setCuisine("chinese");
        var expectedValue = new ArrayList<CuisineDTO>(
                Arrays.asList(val)
        );
        return PaginatedDTO.builder().items(Collections.singletonList(expectedValue)).lastTokens("bnVsbA==").build();
    }
}
