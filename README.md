# About the project RestaurantsAPI

- /getAllCuisines
- /getTopRestaurantsBasedOnRating
- /more APIs are coming.. Thinking about having a service where ML gives recommendation based on what user went in the past by adding more field in an existing DB


**Not in this respository**
- Elastic Search Usage - Still thinking about whether to use Elastic Search or not for caching purpose. Initially it was used for chatbot where a user query to get 3-5 restaurants per cuisine. 

**DynamoDB table schema**
- PrimaryKey: cuisine
- SortKey: name(restaurant name)
- Local Secondary Index: rating - To query rating 
- Other attributes as address, phone number, radius, and etc. 

*Note: Lombok is not used in this project.

- Global Secondary Index: cuisine-global - This is just a flag variable to indicate whether this paticular cuisine has been already marked in DynamoDB to query all cuisines efficiently without scanning the whole table 

**Using AWS API Gateway + AWS Lambda(with Springboot) + DynamoDB + Elastic Search deployed via AWS CDK**

- CDK has only Lambda stack that allows Lambda to access to DynamoDB, APIGateway, and Elastic Search.
- The Lambda hander has a customized APIGatewayProxyResponseEvent handler that is used for routing different API endpoint(API Gateway resource) 
   - Eventually will create another Lambda endpoints when there are more microservices 
   - GET  
      - /getAllCuisines : Take all the cuisines available in NYC
      - /getTopRestaurantsBasedOnRating : Take all the restaurants equal or above the given rating

**Updates** 

Noticed that there are not many resources available for Springboot + Lambda + API Gateway:

I am sharing POM file so anyone who wants to create a quick project can use it. 

Instead of manually handling routing - decided to utilize Springboot `Restcontroller` to automatically route with API Gateway Proxy 
- `SteamLambdaHandler` takes the Springboot application and route with the power of beans
- It is definetely overkill for the routing but this application will have more Lambda endpoints and this will make the application more dynamic
- Springboot3 integration with Lambda reduces the cold start time. 
      - provisional concurrency can reduce start time, but this is not free tier ;) 
- Currently it runs on Java 11. Although Lambda supports Java 17, SAM CLI does support upto Java 11 yet.
