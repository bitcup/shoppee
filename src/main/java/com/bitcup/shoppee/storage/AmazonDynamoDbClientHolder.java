package com.bitcup.shoppee.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author bitcup
 */
public final class AmazonDynamoDbClientHolder {

    private static AmazonDynamoDBClient instance = null;

    protected AmazonDynamoDbClientHolder() {
    }

    public static AmazonDynamoDBClient getInstance() {
        if (instance == null) {
            try {
                Properties prop = new Properties();
                InputStream in = AmazonDynamoDbClientHolder.class.getResourceAsStream("/credz.properties");
                prop.load(in);
                String accessKey = prop.getProperty("accessKey");
                String secretKey = prop.getProperty("secretKey");
                in.close();
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                instance = new AmazonDynamoDBClient(credentials);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return instance;
    }
}
