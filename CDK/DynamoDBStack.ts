import { CfnOutput, RemovalPolicy, Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import { AutoScalingGroup } from "aws-cdk-lib/aws-autoscaling";


export class DynamoDBStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const tableName = "nyc-restaurants";

        const table = new dynamodb.Table(this, 'Table', {
            tableName: tableName,
            partitionKey: {
                name: 'cuisine',
                type: dynamodb.AttributeType.STRING,
            },
            sortKey: {
                name: 'name',
                type: dynamodb.AttributeType.STRING,
            },
            removalPolicy: RemovalPolicy.DESTROY,
        });

        table.autoScaleReadCapacity({
            minCapacity: 1,
            maxCapacity: 1
        }).scaleOnUtilization({
            targetUtilizationPercent: 70,
        });

        table.autoScaleWriteCapacity({
            minCapacity: 1,
            maxCapacity: 1
        }).scaleOnUtilization({
            targetUtilizationPercent: 70,
        });

        table.addLocalSecondaryIndex({
            sortKey: {
                name: 'rating',
                type: dynamodb.AttributeType.STRING,
            },
            indexName: 'rating-index',
            projectionType: dynamodb.ProjectionType.ALL,
        })

        const cusineGlobalIndexName: String = 'cuisine-global-cuisine-index';

        table.addGlobalSecondaryIndex({
            partitionKey: {
                name: 'cuisine-global',
                type: dynamodb.AttributeType.NUMBER,
            },
            sortKey: {
                name: 'cuisine',
                type: dynamodb.AttributeType.STRING,
            },
            indexName: cusineGlobalIndexName.toString(),
        });

        table.autoScaleGlobalSecondaryIndexReadCapacity(cusineGlobalIndexName.toString(),{
            minCapacity: 1,
            maxCapacity: 1
        }).scaleOnUtilization({
            targetUtilizationPercent: 70,
        });

        const customRatingIndexName: String = 'custom-rating-index';

        table.addGlobalSecondaryIndex({
            partitionKey: {
                name: 'custom-rating',
                type: dynamodb.AttributeType.STRING,
            },
            sortKey: {
                name: 'cuisine',
                type: dynamodb.AttributeType.STRING,
            },
            indexName: customRatingIndexName.toString(),
        });

        table.autoScaleGlobalSecondaryIndexReadCapacity(customRatingIndexName.toString(),{
            minCapacity: 1,
            maxCapacity: 1
        }).scaleOnUtilization({
            targetUtilizationPercent: 70,
        });

        new CfnOutput(this, 'TableName', { value: table.tableName });

    }

}
