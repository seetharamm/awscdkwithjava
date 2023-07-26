package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableClass;

public class AwsDynamodbScalingStack extends Stack {
    public AwsDynamodbScalingStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsDynamodbScalingStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

       Table table = Table.Builder.create(this, "dynamotable")
                      .partitionKey(Attribute.builder()
                      .name("canumber")
                      .type(AttributeType.STRING)
                      .build())
                      .tableClass(TableClass.STANDARD)
                      .tableName("CADETAILS")
                      .readCapacity(1)
                      .writeCapacity(1)
                      .removalPolicy(RemovalPolicy.DESTROY)
                      .build();

            
    }
}
