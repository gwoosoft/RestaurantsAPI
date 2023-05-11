import { Duration, Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as opensearch from 'aws-cdk-lib/aws-opensearchservice';

export class LambdaStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const myRole = new iam.Role(this, 'Role', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'), 
    });

    myRole.addToPolicy(new iam.PolicyStatement({
      actions: ["dynamodb:*", "apigateway:*", "es:*", "cloudwatch:*"],
      resources: ["*"],
      effect: iam.Effect.ALLOW,
    }));

    const lambaPath='src/lambda_java/restaurantAPI/target/restaurantLambda.jar';
    const functionName =  `springLambdaHandler`;

    const fn = new lambda.Function(this, functionName, {
      functionName: functionName,
      runtime: lambda.Runtime.JAVA_11,
      handler: 'com.gwsoft.restaurantAPI.StreamLambdaHandler',
      code: lambda.Code.fromAsset(lambaPath),
      role: myRole, 
      memorySize: 3008,
      timeout:Duration.seconds(28),
    });

    fn.addEnvironment('JAVA_TOOL_OPTIONS', '-XX:+TieredCompilation -XX:TieredStopAtLevel=1');


    // (fn.node.defaultChild as lambda.CfnFunction).addPropertyOverride('SnapStart', {
    //   ApplyOn: 'PublishedVersions',
    // });

    // const uniqueLogicalId = `springVersion-${new Date().getMilliseconds()}`
    // // publish a version
    // fn.currentVersion;
    // const kVersion = new lambda.Version(this, uniqueLogicalId, { 
    //   lambda: fn,
    //   description:`snapStart${uniqueLogicalId}`
    // });

   
    
    const lambdaDataSyncName = `lambdaDataSync`;
    const lambdaDataSyncPath = 'src/lambda_java/LF1/target/lambdadatasync.jar';

    const lambdaDataSyncFn = new lambda.Function(this, lambdaDataSyncName, {
      functionName: lambdaDataSyncName,
      runtime: lambda.Runtime.JAVA_11,
      handler: 'org.example.Handler',
      code: lambda.Code.fromAsset(lambdaDataSyncPath),
      role: myRole, 
    });

    const restApi = new apigateway.RestApi(this, "launchapi", { deploy: true,
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
      },
    });

    const apiGatewayLambdaIntegration = new apigateway.LambdaIntegration(fn, {
      proxy: true,
      timeout:Duration.seconds(29),
    }); // to add current version to activate snapstart
    const getAllCuisines = restApi.root.addResource('getAllCuisines'); 
    const getTopRestaurantsBasedOnRating = restApi.root.addResource("getTopRestaurantsBasedOnRating");
    getAllCuisines.addMethod('GET', apiGatewayLambdaIntegration);  // GET /getAllCuisines
    getTopRestaurantsBasedOnRating.addMethod('GET', apiGatewayLambdaIntegration);  // GET /getTopRestaurantsBasedOnRating
  }
}
