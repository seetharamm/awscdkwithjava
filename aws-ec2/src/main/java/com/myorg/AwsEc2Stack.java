package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.s3.assets.Asset;
import software.amazon.awscdk.services.iam.*;


public class AwsEc2Stack extends Stack {
    public AwsEc2Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsEc2Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Vpc vpc = Vpc.Builder.create(this, id + "-vpc")
                .vpcName(id + "-vpc")
                .natGateways(0)
               // .subnetConfiguration(null) // Do not create any NATs
                .build();

         ISecurityGroup securityGroup = SecurityGroup.Builder.create(this, id + "-sg")
                .securityGroupName(id)
                .vpc(vpc)
                .build();

        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(22));
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(80));

        Role ec2role = Role.Builder.create(this, "ec2role")
         .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
         //.description("Example role...")
         .build();
        ec2role.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName("AmazonSSMManagedInstanceCore"));

         Instance ec2Instance = Instance.Builder.create(this, id + "-ec2")
                .instanceName(id + "-ec2")
                .machineImage(MachineImage.latestAmazonLinux())
                .securityGroup(securityGroup)
                .role(ec2role)
                .keyName("myec2keypair")
                .instanceType(InstanceType.of(
                        InstanceClass.BURSTABLE2,
                        InstanceSize.MICRO
                ))
                .vpcSubnets(
                        SubnetSelection.builder()
                                .subnetType(SubnetType.PUBLIC)
                                .build()
                )
                .vpc(vpc)
                .build();

        Asset asset = Asset.Builder.create(this, "Asset")
         .path("./config.sh")
         .build();
       
        String localPath = ec2Instance.getUserData().addS3DownloadCommand(S3DownloadOptions.builder()
         .bucket(asset.getBucket())
         .bucketKey(asset.getS3ObjectKey())
         //.region("us-east-1")
         .build());
        ec2Instance.getUserData().addExecuteFileCommand(ExecuteFileOptions.builder()
         .filePath(localPath)
         .arguments("--verbose -y")
         .build());
       asset.grantRead(ec2Instance.getRole());

        
    }
}
