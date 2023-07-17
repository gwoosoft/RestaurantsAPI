# About the project RestaurantsAPI

- /getAllCuisines
- /getTopRestaurantsBasedOnRating
- /getTopRestaurantsBasedOnRatingByCuisine
- /more APIs are coming.. Thinking about having a service where ML gives recommendation based on what user went in the past by adding more field in an existing DB


**Note**
- As there is no writing action, I did not account for concurrency yet. However, I have a plan to implement opimisitic locking with dynamoDB for some of future CRUD actions
- Most errors are handled in controller level as recommended practice for controlling program flow.
- Minor bug in validating rating. It will throw validation error if you try to put decimal value. 

**Not in this respository**
- Elastic Search Usage - Still thinking about whether to use Elastic Search or not for caching purpose(or LSU cache for data sync mechanism to update the most latest restaurants as there is no way there will be 100 restaurants changes per week in nyc. A problem with this approach is that I would not be able to check when a restaurant goes down.

**DynamoDB table schema**
- PrimaryKey: cuisine
- SortKey: name(restaurant name)
- Local Secondary Index: rating - To query rating 
- Other attributes as address, phone number, radius, and etc. 
- Global Secondary Index: cuisine-global - This is just a flag variable to indicate whether this paticular cuisine has been already marked in DynamoDB to query all cuisines efficiently without scanning the whole table 

**Using AWS API Gateway + AWS Lambda(with Springboot) + DynamoDB and infrastructure created via AWS CDK**

- CDK has only Lambda stack that allows Lambda to access to DynamoDB, APIGateway, and Elastic Search(commented it out).
- The Lambda hander has a customized APIGatewayProxyResponseEvent handler that is used for routing different API endpoint(API Gateway resource) 
   - Eventually will create another Lambda endpoints when there are more microservices 
   - GET 
      - /getAllCuisines?maxNum={Integer}&lastEvaluatedKey={String}: Take all the cuisines available in NYC, it has pagination and lastEvaluatedKey response with Base64 encoded
      - /getTopRestaurantsBasedOnRating?maxNum={Integer}&lastEvaluatedKey={String}&rating={String} : Take all the restaurants equal or above the given rating, it has pagination and lastEvaluatedKey response with Base64 encoded
      - /getTopRestaurantsBasedOnRatingByCuisine?cusine?{string}&maxNum={Integer}&lastEvaluatedKey={String}&rating={String} : based on rating by the cuisine, it has pagination and lastEvaluatedKey response with Base64 encoded
      
```
{
    "items": [
        {
            "cuisine": "korean",
            "name": "Jongro BBQ",
            "id": "FZpm4_fbd6P984foOUejWg",
            "reviewCount": 2468,
            "rating": 4.0,
            "address1": "22 W 32nd St",
            "zipCode": null,
            "phone": "+12124732233",
            "cuisineGlobal": null
        },
        {
            "cuisine": "korean",
            "name": "Bokki Seoul Food",
            "id": "uC5_J2l9uwTleh7j5W__kg",
            "reviewCount": 46,
            "rating": 4.0,
            "address1": "374 Pearl St",
            "zipCode": null,
            "phone": "+17182431342",
            "cuisineGlobal": null
        },
        {
            "cuisine": "korean",
            "name": "Woorijip",
            "id": "ogCC-lJJYnwXDvKGmKZ6Sw",
            "reviewCount": 2847,
            "rating": 4.0,
            "address1": "12 W 32nd St",
            "zipCode": null,
            "phone": "+12122441115",
            "cuisineGlobal": null
        }
    ],
    "lastTokens": "eyJyYXRpbmciOnsibiI6IjQifSwibmFtZSI6eyJzIjoiV29vcmlqaXAifSwiY3Vpc2luZSI6eyJzIjoia29yZWFuIn19"
}
```


I noticed that there are not many resources available for Springboot + Lambda + API Gateway so I am sharing POM file so anyone who wants to create a quick project can use it. 

Instead of manually handling routing - decided to utilize Springboot `Restcontroller` to automatically route with API Gateway Proxy 
- `SteamLambdaHandler` takes the Springboot application and route with the power of beans
- It is definetely overkill just for the routing but this application will have more Lambda endpoints and this will make the application more dynamic, meanwhile migration work from Lambda to something else will be a lot easier. 
- Springboot3 integration with Lambda reduces the cold start time. 

      - provisional concurrency can reduce start time, but this is not free tier ;).
      - Instead of provisional concunrency, I implemented Lambda Snapstart to reduce the start time.

```
RESTORE_START Runtime Version: java:11.v19	Runtime Version ARN: arn:aws:lambda:us-east-1::runtime:bd472a12ac73ffacf794b0457d28eab67af5f7b9989803e4cd445069206b06b2
RESTORE_REPORT Restore Duration: 509.31 ms // this indicates Snapstart is in place 
START RequestId: c0c7298c-39c1-4b90-9c2d-1b2a7e8437ff Version: 2
```

- Currently it runs on Java 11. Although Lambda supports Java 17, SAM CLI does support upto Java 11 yet.
