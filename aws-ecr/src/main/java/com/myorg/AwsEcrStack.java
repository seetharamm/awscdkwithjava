package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecr.*;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsEcrStack extends Stack {
    public AwsEcrStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsEcrStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Repository repository = Repository.Builder.create(this, "Repository")
                                .imageScanOnPush(true)
                                .build();
        
    }
}
