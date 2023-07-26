package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

@SuppressWarnings("unused")
public class LambdaFunctionHandler implements RequestHandler<Object, APIGatewayProxyResponseEvent> {

	private static S3Client s3;
    @Override
    public APIGatewayProxyResponseEvent handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);
        JSONObject resultObj = new JSONObject();
        List<String> resultData = new ArrayList<String>();
        resultData.add("one");
        resultData.add("two");
        resultObj.put("successful",true);		
		resultObj.put("dataset", resultData.toString());
		
		
		
		
		APIGatewayProxyResponseEvent resp = new APIGatewayProxyResponseEvent()
		        .withStatusCode(200)
		        .withBody("Hello from lambda")
		        .withIsBase64Encoded(false);
		//String response = resultObj.toString();
        // TODO: implement your handler
		context.getLogger().log("resp: " + resp);
        return resp;
    }

}
