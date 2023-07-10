package com.gwsoft.restaurantAPI.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.google.gson.Gson;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

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

    @Test public void ListCuisines(){

        QueryResultPage<RestaurantEntity> queryResultPage = new QueryResultPage<>();
        List<RestaurantEntity> results = createMockQueryResultPage();
        queryResultPage.setResults(results);

        var expectedValue = new ArrayList<CuisineDTO>();
        var val = new CuisineDTO();
        val.setCuisine("chinese");
        expectedValue.add(val);

        when(dynamoDBRepository.getAllCuisines(null,1)).thenReturn(queryResultPage);
        when(restaurantServiceHelper.getRestaurantDtoList(queryResultPage)).thenCallRealMethod();

        PaginatedDTO returnValue = restaurantService.ListCuisines(1, null);

        Assert.assertNotNull(returnValue);


        var expectedReturnValue = new PaginatedDTO(expectedValue, "bnVsbA\u003d\u003d");
        var actual = gsonHelper.toJson(returnValue.getItems());
        var expected = gsonHelper.toJson(expectedReturnValue.getItems());

        System.out.println("show me actual:" + actual);
        System.out.println("show me expected:" + expected);

        Assert.assertEquals(expected, actual);
    }

    public List createMockQueryResultPage(){
        RestaurantEntity mockData = new RestaurantEntity();
        mockData.setCuisineGlobal(1);
        mockData.setCuisine("chinese");
        List<RestaurantEntity> results = new ArrayList<>();
        results.add(mockData);
        return results;
    }
}
