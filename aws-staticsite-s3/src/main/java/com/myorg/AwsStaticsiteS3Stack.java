package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.services.certificatemanager.DnsValidatedCertificate;
import software.amazon.awscdk.services.cloudfront.*;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Version;
import software.amazon.awscdk.services.route53.*;
import software.amazon.awscdk.services.route53.patterns.HttpsRedirect;
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;
import java.util.*;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;

public class AwsStaticsiteS3Stack extends Stack {
    public AwsStaticsiteS3Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsStaticsiteS3Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
   String domainName = "omicomsolutions.com";
        IHostedZone hostedZone = HostedZone.fromLookup(this, "HostedZone", HostedZoneProviderProps.builder()
        .domainName(domainName)
        .build());

    DnsValidatedCertificate websiteCertificate = DnsValidatedCertificate.Builder.create(this, "WebsiteCertificate")
        .hostedZone(hostedZone)
        .region("us-east-1")
        .domainName(domainName)
        .subjectAlternativeNames(List.of(String.format("www.%s", domainName)))
        .build();

    // commented out purely so I don't have to pay someone deciding to hit my endpoint in a for loop...
    //        HelloWorldApi helloWorldApi = new HelloWorldApi(this, "HelloWorldApi", props, stackConfig);

    // S3 bucket we'll use for storing our website in
    Bucket websiteBucket = Bucket.Builder.create(this, "WebsiteBucket")
        .bucketName(String.format("website-%s", props.getEnv().getAccount()))
        .encryption(BucketEncryption.UNENCRYPTED)
        .websiteIndexDocument("index.html")
        .removalPolicy(RemovalPolicy.DESTROY)
        .build();

    OriginAccessIdentity webOai = OriginAccessIdentity.Builder.create(this, "WebOai")
        .comment(String.format("OriginAccessIdentity for %s", domainName))
        .build();


    websiteBucket.grantRead(webOai);

   /*  AwsCustomResource lambdaParameter = AwsCustomResource.Builder.create(this, "LambdaParameter")
        .policy(AwsCustomResourcePolicy.fromStatements(List.of(
            PolicyStatement.Builder.create()
            .effect(Effect.ALLOW)
            .actions(List.of("ssm:GetParameter*"))
            .resources(List.of(formatArn(ArnComponents.builder()
                .service("ssm")
                .region("us-east-1")
                .resource("parameter/blog/lambdaEdgeLambdaVersion")
                .build())))
            .build())))
        .onUpdate(AwsSdkCall.builder()
            .service("SSM")
            .action("getParameter")
            .parameters(Map.of("Name", "/blog/lambdaEdgeLambdaVersion"))
            .region("us-east-1")
            .physicalResourceId(PhysicalResourceId.of(new Date().toString()))
            .build())
        .build(); */

        CloudFrontWebDistribution distribution =
              CloudFrontWebDistribution.Builder.create(this, "SiteDistribution")
                      .viewerCertificate(ViewerCertificate.fromAcmCertificate(websiteCertificate, ViewerCertificateOptions
                              .builder()
                              .aliases(domainName)
                              .sslMethod(SSLMethod.SNI)
                              .securityPolicy(SecurityPolicyProtocol.TLS_V1_1_2016)
                              .build()
              ))
              .originConfigs(sourceConfigurationsList)
              .build();

    HttpsRedirect webHttpsRedirect = HttpsRedirect.Builder.create(this, "WebHttpsRedirect")
        .certificate(websiteCertificate)
        .recordNames(List.of(String.format("www.%s", domainName)))
        .targetDomain(domainName)
        .zone(hostedZone)
        .build();


    ARecord apexARecord = ARecord.Builder.create(this, "ApexARecord")
        .recordName(domainName)
        .zone(hostedZone)
        .target(RecordTarget.fromAlias(new CloudFrontTarget(cloudFrontWebDistribution)))
        .build();
    }
}
