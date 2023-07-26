package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.*;
import software.amazon.awscdk.services.s3.deployment.*;
import java.util.*;
import software.amazon.awscdk.RemovalPolicy;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsStaticsiteWithoutCdnStack extends Stack {
    public AwsStaticsiteWithoutCdnStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsStaticsiteWithoutCdnStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

       Bucket bucket = Bucket.Builder.create(this, "MyBucket")
                           .versioned(true)
                           .bucketName("omicomsolutions.com")
                           .encryption(BucketEncryption.UNENCRYPTED)
                           .websiteIndexDocument("index.html")
                           .publicReadAccess(true)
                           .removalPolicy(RemovalPolicy.DESTROY)
                           .build();

        BucketDeployment.Builder.create(this, "DeployWebsite")
         .sources(List.of(Source.asset("./site-contents")))
         .destinationBucket(bucket)
         //.destinationKeyPrefix("web/static")
         .build();
    }
}
