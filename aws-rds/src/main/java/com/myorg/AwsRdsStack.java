package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.CfnOutput;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsRdsStack extends Stack {
    public AwsRdsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsRdsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        final Vpc vpc = Vpc.Builder.create(this, id + "-vpc")
                .natGateways(1) // Do not create any gateways
                .build();

        final IInstanceEngine instanceEngine = DatabaseInstanceEngine.postgres(
                PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_13_6)
                        .build()
        );


//        final IInstanceEngine instanceEngine = DatabaseInstanceEngine.mysql(
//                MySqlInstanceEngineProps.builder()
//                        .version(MysqlEngineVersion.VER_8_0_30)
//                        .build()
//        ); credentials: rds.Credentials.fromGeneratedSecret('syscdk')

        final DatabaseInstance databaseInstance = DatabaseInstance.Builder.create(this, id + "-rds")
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO))
                .engine(instanceEngine)
                .instanceIdentifier(id + "-rds")
                .credentials(Credentials.fromGeneratedSecret("mysecret"))
                .databaseName("cadetails")
                .removalPolicy(RemovalPolicy.DESTROY) // If you want the destroy command to not take the final snapshot
                .build();
        databaseInstance.getConnections().allowDefaultPortFromAnyIpv4();
                
        //databaseInstance.getConnections().allowFrom("*", 5432);
                CfnOutput.Builder.create(this, "port")
                .value(databaseInstance.getDbInstanceEndpointPort()).build();
                 CfnOutput.Builder.create(this, "endpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress()).build();
                 CfnOutput.Builder.create(this, "secretname")
                .value(databaseInstance.getSecret().getSecretName()).build();

    }
}
