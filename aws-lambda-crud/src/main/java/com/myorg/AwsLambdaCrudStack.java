package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import java.util.HashMap;
import java.util.Map;


public class AwsLambdaCrudStack extends Stack {
    public AwsLambdaCrudStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsLambdaCrudStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
       


        TableProps tableProps;
        Attribute partitionKey = Attribute.builder()
                .name("caNumber")
                .type(AttributeType.STRING)
                .build();
        tableProps = TableProps.builder()
                .tableName("cadetails")
                .partitionKey(partitionKey)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        Table dynamodbTable = new Table(this, "cadetails", tableProps);


        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY","caNumber");


        Function listCaDetails = Function.Builder.create(this,"listCaDetailFunction")
                               .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                               .handler("software.amazon.awscdk.examples.lambda.GetAllItems")
                               .runtime(Runtime.JAVA_8)
                               .environment(lambdaEnvMap)
                               .timeout(Duration.seconds(30))
                               .memorySize(512)
                               .build();
        Function createCaDetails = Function.Builder.create(this,"createCaDetails")
                               .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                               .handler("software.amazon.awscdk.examples.lambda.CreateItem")
                               .runtime(Runtime.JAVA_8)
                               .environment(lambdaEnvMap)
                               .timeout(Duration.seconds(30))
                               .memorySize(512)
                               .build();

       Function listCaDetail = Function.Builder.create(this,"getCaDetailFunction")
                               .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                               .handler("software.amazon.awscdk.examples.lambda.GetOneItem")
                               .runtime(Runtime.JAVA_8)
                               .environment(lambdaEnvMap)
                               .timeout(Duration.seconds(30))
                               .memorySize(512)
                               .build();
        Function deleteCaDetails = Function.Builder.create(this,"deleteCaDetailsFunction")
                               .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                               .handler("software.amazon.awscdk.examples.lambda.DeleteItem")
                               .runtime(Runtime.JAVA_8)
                               .environment(lambdaEnvMap)
                               .timeout(Duration.seconds(30))
                               .memorySize(512)
                               .build();

        Function updateCaDetails = Function.Builder.create(this,"updateCaDetailsFunction")
                               .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                               .handler("software.amazon.awscdk.examples.lambda.UpdateItem")
                               .runtime(Runtime.JAVA_8)
                               .environment(lambdaEnvMap)
                               .timeout(Duration.seconds(30))
                               .memorySize(512)
                               .build();

                               dynamodbTable.grantReadWriteData(listCaDetails);
                               dynamodbTable.grantReadWriteData(createCaDetails);
                               dynamodbTable.grantReadWriteData(listCaDetail);
                               dynamodbTable.grantReadWriteData(updateCaDetails);
                               dynamodbTable.grantReadWriteData(deleteCaDetails);

        RestApi api = new RestApi(this,"caDetails",RestApiProps.builder().restApiName("myrestapi").build());
       IResource v1 = api.getRoot().addResource("v1");
       LambdaIntegration listIntegration = new LambdaIntegration(listCaDetails);
       v1.addMethod("GET", listIntegration);

       LambdaIntegration createIntegration = new LambdaIntegration(createCaDetails);
       v1.addMethod("POST", createIntegration);

       IResource getCaDetails = v1.addResource("{id}");

       LambdaIntegration getIntegration = new LambdaIntegration(listCaDetail);
       LambdaIntegration updateIntegration = new LambdaIntegration(updateCaDetails);
       LambdaIntegration deleteIntegration = new LambdaIntegration(deleteCaDetails);
       getCaDetails.addMethod("GET", getIntegration) ;  
       getCaDetails.addMethod("PUT", updateIntegration) ;   
       getCaDetails.addMethod("DELETE", deleteIntegration) ;  
       
       
                      

       
    }
}
