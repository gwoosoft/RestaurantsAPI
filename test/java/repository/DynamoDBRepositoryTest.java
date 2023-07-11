package com.gwsoft.restaurantAPI.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.gwsoft.restaurantAPI.model.RestaurantEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
public class DynamoDBRepositoryTest {

    @Mock
    DynamoDBMapper dynamoDBMapper;

    @InjectMocks DynamoDBRepository dynamoDBRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllCuisines(){

        QueryResultPage<RestaurantEntity> queryResultPage = new QueryResultPage<>();
        List<RestaurantEntity> results = createMockQueryResultPage();
        queryResultPage.setResults(results);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setCuisineGlobal(1);

        DynamoDBQueryExpression<RestaurantEntity> queryExpression =  new DynamoDBQueryExpression<RestaurantEntity>()
                .withExclusiveStartKey(null)
                .withHashKeyValues(restaurantEntity)
                .withLimit(1)
                .withIndexName("cuisine-global-cuisine-index")
                .withConsistentRead(false);

        // Set expectation
        Mockito.when(dynamoDBMapper.queryPage(eq(RestaurantEntity.class), Mockito.any())).thenReturn(queryResultPage);

        // Set actual value
        var returnValue = dynamoDBRepository.getAllCuisines(null, 1);
        System.out.println("returnValue:" + returnValue);
        Assert.assertNotNull(returnValue);
        Assert.assertEquals(returnValue, queryResultPage);
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
