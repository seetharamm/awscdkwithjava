package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.cloudwatch.*;
import software.amazon.awscdk.services.cloudwatch.actions.*;
// import software.amazon.awscdk.Duration;
 import software.amazon.awscdk.services.sns.*;
 import java.util.*;
import software.amazon.awscdk.services.sns.subscriptions.*;

public class AwsCloudWatchAlarmsStack extends Stack {
    public AwsCloudWatchAlarmsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsCloudWatchAlarmsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        SingletonFunction listCaDetails =
        SingletonFunction.Builder.create(this, "asset-lambda")
        .description("asset-lambda")
        .handler("index.main")
        .code(Code.fromAsset("./assets"))
        .runtime(Runtime.PYTHON_3_8)
        .uuid(UUID.randomUUID().toString())
        .timeout(Duration.seconds(300))
        .build();

       

       Alarm alarm =  listCaDetails.metricErrors().createAlarm(this, "Alarm", CreateAlarmOptions.builder()
         .threshold(2)
         .evaluationPeriods(1)
         .comparisonOperator(ComparisonOperator.LESS_THAN_OR_EQUAL_TO_THRESHOLD)
         .build());

         Topic topic = new Topic(this, "Topic");
         topic.addSubscription(new EmailSubscription("kondapaneni.sitaramaiah@lntinfotech.com"));
         alarm.addAlarmAction(new SnsAction(topic));

    }
}
