package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Function;

import software.amazon.awscdk.services.lambda.destinations.*;
import software.amazon.awscdk.services.lambda.eventsources.*;
import software.amazon.awscdk.services.sns.subscriptions.*;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.s3.*;
import java.util.*;

public class AwsLambdaEvountsourcesS3Stack extends Stack {
    public AwsLambdaEvountsourcesS3Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsLambdaEvountsourcesS3Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Topic dest = new Topic(this, "dest topic");
        dest.addSubscription(new EmailSubscription("kondapaneni.sitaramaiah@lntinfotech.com"));

        Function myFn = Function.Builder.create(this, "Fn")
                .handler("com.amazonaws.lambda.demo.LambdaFunctionHandler")
                .code(Code.fromAsset("./assets/demo-1.0.0.jar"))
                .runtime(Runtime.JAVA_8)
                .onSuccess(new SnsDestination(dest))
                .build();

        Bucket bucket = new Bucket(this, "mybucket");

        myFn.addEventSource(S3EventSource.Builder.create(bucket)
                .events(List.of(EventType.OBJECT_CREATED, EventType.OBJECT_REMOVED))
                .filters(List.of(NotificationKeyFilter.builder().prefix("app/").build()))
                .build());
    }
}
