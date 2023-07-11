package com.gwsoft.restaurantAPI;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RestaurantAPIApplicationTests {
    @Test
    public void testGetProducts_ShouldReturnProductsArray()
    {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .when()
                .get("${yourAPIEndPpoint}/prod/getAllCuisines");

        Assertions.assertEquals(response.statusCode(), 200);
        String responseBody = response.asString();
        Assertions.assertNotNull(responseBody);
    }
}
