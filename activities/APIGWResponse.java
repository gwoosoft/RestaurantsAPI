package org.example.activities;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.simple.JSONObject;

public class APIGWResponse  {
    public APIGatewayProxyResponseEvent handleResponse(Integer statusCode, JSONObject responseBody){
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(responseBody.toString())
                .withIsBase64Encoded(false);
    };
}
