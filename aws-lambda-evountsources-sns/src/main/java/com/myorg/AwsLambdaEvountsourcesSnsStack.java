package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.destinations.*;
import software.amazon.awscdk.services.lambda.eventsources.*;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.sns.subscriptions.*;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.lambda.Runtime;
import java.util.*;

public class AwsLambdaEvountsourcesSnsStack extends Stack {
    public AwsLambdaEvountsourcesSnsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsLambdaEvountsourcesSnsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Topic topic = new Topic(this, "Topic");
        topic.addSubscription(new EmailSubscription("kondapaneni.sitaramaiah@lntinfotech.com"));

        Topic destinationTopic = new Topic(this, "destination topic");
        destinationTopic.addSubscription(new EmailSubscription("kondapaneni.sitaramaiah@lntinfotech.com"));

        Function myFn = Function.Builder.create(this, "Fn")
                .handler("com.amazonaws.lambda.demo.LambdaFunctionHandler")
                .code(Code.fromAsset("./assets/demo-1.0.0.jar"))
                .runtime(Runtime.JAVA_8)
                .timeout(Duration.minutes(5))
                .onSuccess(new SnsDestination(destinationTopic))
                .build();
        Queue deadLetterQueue = new Queue(this, "deadLetterQueue");
        myFn.addEventSource(SnsEventSource.Builder.create(topic)
                .filterPolicy(Map.of())
                .deadLetterQueue(deadLetterQueue)
                .build());

    }
}
