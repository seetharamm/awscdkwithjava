package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.StartingPosition;
import software.amazon.awscdk.services.kinesis.*;
import software.amazon.awscdk.services.lambda.eventsources.KinesisEventSource;
import software.amazon.awscdk.services.lambda.destinations.*;

import software.amazon.awscdk.services.sns.subscriptions.*;
import software.amazon.awscdk.services.sns.Topic;

public class AwsLambdaEvountsourcesKinesisStack extends Stack {
    public AwsLambdaEvountsourcesKinesisStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsLambdaEvountsourcesKinesisStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        

        Topic dest = new Topic(this, "kinesis topic");
        dest.addSubscription(new EmailSubscription("kondapaneni.sitaramaiah@lntinfotech.com"));

        Function myFn = Function.Builder.create(this, "Fn")
                .handler("com.amazonaws.lambda.demo.LambdaFunctionHandler")
                .code(Code.fromAsset("./assets/demo-1.0.0.jar"))
                .runtime(Runtime.JAVA_8)
                .onSuccess(new SnsDestination(dest))
                .build();
        Stream stream = new Stream(this, "MyStream");
        myFn.addEventSource(KinesisEventSource.Builder.create(stream)
                .batchSize(2) 
                .startingPosition(StartingPosition.TRIM_HORIZON)
                .build());
    }
}
