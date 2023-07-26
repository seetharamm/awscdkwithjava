package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.apigateway.PassthroughBehavior;
import java.util.*;
import software.amazon.awscdk.services.lambda.Runtime;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class ApigatewayTokenAuthorizerStack extends Stack {
    public ApigatewayTokenAuthorizerStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApigatewayTokenAuthorizerStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function authorizerFn = Function.Builder.create(this, "MyAuthorizerFunction")
        .runtime(Runtime.NODEJS_14_X)
        .handler("index.handler")
        .code(AssetCode.fromAsset( "./assets/authorizer.handler"))
        .build();

Object authorizer = TokenAuthorizer.Builder.create(this, "MyAuthorizer")
        .handler(authorizerFn)
        .build();

RestApi restapi = RestApi.Builder.create(this, "MyRestApi")
        .cloudWatchRole(true)
        .defaultMethodOptions(Map.of(
                "authorizer", authorizer))
        .build();

        restapi.getRoot().addMethod("ANY", MockIntegration.Builder.create()
        .integrationResponses(List.of(Map.of("statusCode", "200")))
        .passthroughBehavior(PassthroughBehavior.NEVER)
        .requestTemplates(Map.of(
                "application/json", "{ \"statusCode\": 200 }"))
        .build(), Map.of(
        "methodResponses", List.of(Map.of("statusCode", "200"))));
    }
}
