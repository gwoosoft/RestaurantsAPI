# About the project RestaurantsAPI

- /getAllCuisines
- /getTopRestaurantsBasedOnRating
- /more query is coming.. Thinking about having a service where ML gives recommendation based on what user went in the past by adding more field in an existing DB

**Not in this respository**
- Data sync lambda function that collects Data from API and save them in DynamoDB with 
- Elastic Search Usage - Still thinking about whether to use Elastic Search or not for caching purpose. Initially it was used for chatbot where a user query to get 3-5 restaurants per cuisine. 

**DynamoDB table schema**
- PrimaryKey: cuisine
- SortKey: name(restaurant name)
- Local Index: rating - To query rating 
- other attributes as address, phone number, radius, and etc. 

- Global Secondary Index: cuisine-global - where it is a flag to indicate that this cuisine has been already marked - To query all cuisines efficiently without scanning the whole table 

**Using AWS API Gateway + AWS Lambda + DynamoDB + Elastic Search deployed via AWS CDK**

- CDK has only Lambda stack that allows Lambda to access to DynamoDB, APIGateway, and Elastic Search.
- The Lambda hander has a customized APIGatewayProxyResponseEvent handler that is used for routing different API endpoint(API Gateway resource) 
   - GET  
      - /getAllCuisines
      - /getTopRestaurantsBasedOnRating



