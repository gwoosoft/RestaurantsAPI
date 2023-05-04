package org.example;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.util.Map;

public class HandlerString implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent>{
    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> event, Context context) {
        return new ProxyHandler().ProxyResponse(event);
    }
}