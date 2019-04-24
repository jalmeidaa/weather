package com.intuit.services.modules;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.intuit.services.core.QErrorResponse;
import com.intuit.services.core.QResponse;
import com.intuit.services.core.QSuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherBO {
    private static Logger logger = LoggerFactory.getLogger(WeatherBO.class);
    private static final String StreamName = "weather-stream";
    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = null;
    AmazonKinesis kinesis;
    AmazonKinesisClientBuilder kinesisClientBuilder;

    public WeatherBO() {
        gsonBuilder.serializeNulls();
        gson = gsonBuilder.create();
        final AWSCredentials awsCredentials = new AWSCredentials() {
            public String getAWSAccessKeyId() {
                return ACCESS_KEY;
            }

            public String getAWSSecretKey() {
                return SECRET_KEY;
            }
        };
        AWSCredentialsProvider credentials = new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return awsCredentials;
            }

            public void refresh() {
            }
        };
        kinesisClientBuilder = AmazonKinesisClientBuilder.standard();
        kinesisClientBuilder.withCredentials(credentials);
        kinesis = kinesisClientBuilder.build();
    }

    public QResponse create(String body) {
        try {
            logger.info("Submitted to stream");
            JsonObject data = (JsonObject) gson.fromJson(body, JsonObject.class);
            String rep = writeToStream(data);
            return new QSuccessResponse(rep);
        } catch (Exception e) {
            logger.info("Submit to Stream failed with " + e.getMessage());
            return new QErrorResponse(new QErrorResponse.Error(0, e.getMessage(), e.getStackTrace()));
        }
    }

    public String writeToStream(JsonObject input) {
        Charset utf8 = Charset.forName("UTF-8");
        PutRecordRequest putRecordRequest = new PutRecordRequest();
        putRecordRequest.setStreamName(StreamName);
        putRecordRequest.setPartitionKey("W1");
        putRecordRequest.setData(ByteBuffer.wrap(input.toString().getBytes(utf8)));

        PutRecordResult putRecordResult = kinesis.putRecord(putRecordRequest);

        return putRecordResult.getSequenceNumber();
    }
}
