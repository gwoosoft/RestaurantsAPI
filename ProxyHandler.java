package org.example;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.example.activities.Service;
import org.example.model.ProxyEvent;
import org.json.simple.JSONObject;

import java.util.Map;

public class ProxyHandler {
    public APIGatewayProxyResponseEvent ProxyResponse(Map<String, Object> event){

        Service service = new Service();
        ProxyEvent proxyEvent = new ProxyEvent(event);


        // TODO: implement pagination
//        String cuisine = ((Map<String, String>)(event.get("queryStringParameters"))).get("cuisine");
//        Map<String,AttributeValue> lastEvaluatedKey = ((Map<String, Map<String,AttributeValue>>)(event.get("queryStringParameters"))).get("lastEvaluatedKey"); // we will send this as response and use this as exclusive start
//        Integer maxNum = ((Map<String, Integer>)(event.get("queryStringParameters"))).get("maxNum");

        JSONObject response = new JSONObject();

        if((proxyEvent.getHttpMethod()).equals(ProxyEvent.Method.GET.toString())) {
            try {
                switch (((String)proxyEvent.getResource())) {
                    case "/getAllCuisines":
                        return service.getAllCuisines();
                    case "/getTopRestaurantsBasedOnRating":
                        String rating = ((Map<String, String>) (event.get("queryStringParameters"))).get("rating");
                        return service.getTopRestaurantsBasedOnRating(rating);
                }
            } catch (Exception e) {
                System.out.println("Failing error");
                response.put("failing reason", ((String)e));
            }
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withBody(response.toString())
                .withIsBase64Encoded(false);

    }
}
