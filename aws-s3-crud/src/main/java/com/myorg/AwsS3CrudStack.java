package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.*;
import software.amazon.awscdk.services.lambda.*; 
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.apigateway.*;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsS3CrudStack extends Stack {
    public AwsS3CrudStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsS3CrudStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket bucket = Bucket.Builder.create(this, "MyBucket")
                           .versioned(true)
                           .bucketName("s3crud.com")
                           .encryption(BucketEncryption.UNENCRYPTED)
                           //.websiteIndexDocument("index.html")
                           //.publicReadAccess(true)
                           .removalPolicy(RemovalPolicy.DESTROY)
                           .build();

         Function lambda = Function.Builder.create(this,"HelloLambda")
       
        .handler("com.amazonaws.lambda.demo.LambdaFunctionHandler")
        .code(Code.fromAsset("./assets/demo-1.0.0.jar"))
        .runtime(Runtime.JAVA_8)
        .timeout(Duration.minutes(5)).build();

        RestApi api = new RestApi(this, "myrestApi",
                      RestApiProps.builder().restApiName("restApiName").build());

           IResource v1   = api.getRoot().addResource("v1");
           LambdaIntegration li = new LambdaIntegration(lambda);
           v1.addMethod("GET",li);

        
    }
}
