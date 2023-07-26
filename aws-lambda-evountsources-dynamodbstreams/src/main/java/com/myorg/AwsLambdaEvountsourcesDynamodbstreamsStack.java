package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.StartingPosition;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.StreamViewType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;

import software.amazon.awscdk.services.lambda.destinations.*;
import software.amazon.awscdk.services.lambda.eventsources.DynamoEventSource;
import software.amazon.awscdk.services.lambda.eventsources.*;
import software.amazon.awscdk.services.sns.subscriptions.*;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sqs.Queue;

public class AwsLambdaEvountsourcesDynamodbstreamsStack extends Stack {
    public AwsLambdaEvountsourcesDynamodbstreamsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsLambdaEvountsourcesDynamodbstreamsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        TableProps tableProps;
        Attribute partitionKey = Attribute.builder()
                .name("caNumber")
                .type(AttributeType.STRING)
                .build();
        tableProps = TableProps.builder()
                .tableName("cadetails")
                .partitionKey(partitionKey)
                .stream(StreamViewType.NEW_AND_OLD_IMAGES)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        Table dynamodbTable = new Table(this, "cadetails", tableProps);

        Topic destinationTopic = new Topic(this, "destination topic");
        destinationTopic.addSubscription(new EmailSubscription("kondapaneni.sitaramaiah@lntinfotech.com"));

        Function myFn = Function.Builder.create(this, "Fn")
                .handler("com.amazonaws.lambda.demo.LambdaFunctionHandler")
                .code(Code.fromAsset("./assets/demo-1.0.0.jar"))
                .runtime(Runtime.JAVA_8)
                .onSuccess(new SnsDestination(destinationTopic))
                .build();

        Queue deadLetterQueue = new Queue(this, "deadLetterQueue");
        myFn.addEventSource(DynamoEventSource.Builder.create(dynamodbTable)
                .startingPosition(StartingPosition.TRIM_HORIZON)
                .batchSize(5)
                .bisectBatchOnError(true)
                .onFailure(new SqsDlq(deadLetterQueue))
                .retryAttempts(2)
                .build());
    }
}
