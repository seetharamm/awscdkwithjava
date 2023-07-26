package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.*;
import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsS3LificycleExpirationStack extends Stack {
    public AwsS3LificycleExpirationStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsS3LificycleExpirationStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

       Bucket bucket = new Bucket(this, "mybucket");
       

      LifecycleRule lifecycleRule = LifecycleRule.builder()
              .abortIncompleteMultipartUploadAfter(Duration.minutes(30))
              .enabled(false)
              .expiration(Duration.minutes(30))
              .expiredObjectDeleteMarker(false)
              .id("myrule")
              .build();

      bucket.addLifecycleRule(lifecycleRule);
    }
}
