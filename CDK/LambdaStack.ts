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
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),   // required
    });

    myRole.addToPolicy(new iam.PolicyStatement({
      actions: ["dynamodb:*", "apigateway:*", "es:*"],
      resources: ["*"],
      effect: iam.Effect.ALLOW,
    }));

    
    const lambaPath='src/lambda_java/untitled/target/lambdajavaone.jar';
    const functionName =  `lambdaJavaOne`;

    const fn = new lambda.Function(this, functionName, {
      functionName: functionName,
      runtime: lambda.Runtime.JAVA_11,
      handler: 'org.example.HandlerString',
      code: lambda.Code.fromAsset(lambaPath),
      role: myRole, 
    });

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

    // const devDomain = new opensearch.Domain(this, 'photosearch', {
    //   version: opensearch.EngineVersion.OPENSEARCH_1_3,
    //   capacity: {
    //     masterNodes:3,
    //     dataNodes: 2,
    //     masterNodeInstanceType: "t3.small.search",
    //   },
    //   ebs: {
    //     volumeSize: 10,
    //   },
    //   zoneAwareness: {
    //     enabled:true,
    //     availabilityZoneCount: 2,
    //   },
    //   logging: {
    //     slowSearchLogEnabled: true,
    //     appLogEnabled: true,
    //     slowIndexLogEnabled: true,
    //   },
    // });
    
    const apiGatewayLambdaIntegration = new apigateway.LambdaIntegration(fn, {proxy: true,});

    const getAllCuisines = restApi.root.addResource('getAllCuisines'); 
    const getTopRestaurantsBasedOnRating = restApi.root.addResource("getTopRestaurantsBasedOnRating");
    getAllCuisines.addMethod('GET', apiGatewayLambdaIntegration);  // GET /getAllCuisines
    getTopRestaurantsBasedOnRating.addMethod('GET', apiGatewayLambdaIntegration);  // GET /getTopRestaurantsBasedOnRating
  }
}
