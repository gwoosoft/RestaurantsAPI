package com.gwsoft.restaurantAPI;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class StreamLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    private static Logger logger = LoggerFactory.getLogger(StreamLambdaHandler.class);
    static {
        try {
            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
                    .defaultProxy()
                    .asyncInit()
                    .springBootApplication(RestaurantAPIApplication.class)
                    .profiles("lambda")
                    .buildAndInitialize();
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
            logger.error("Cannot initialize Spring container", e);
            throw new RuntimeException("Could not initialize Spring framework", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        try{
            AwsProxyResponse res = handler.proxy(awsProxyRequest, context);
            Map<String, String> headers = new HashMap<>();
            headers.put("Access-Control-Allow-Headers","Content-Type");
            headers.put("Access-Control-Allow-Origin", "*");
            headers.put("Access-Control-Allow-Methods", "*");
            res.setHeaders(headers);
            return res;
        }catch (Exception e){
            throw new RuntimeException("Error gettting proxy with error:" + e);
        }
    }
}
