package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.example.utills.YelpAPI;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, Void> {

    @Override
    public Void handleRequest(Map<String, Object> input, Context context) {
        YelpAPI yelpAPI = new YelpAPI();
        yelpAPI.getYelpApi();
        return null;
    }
}
