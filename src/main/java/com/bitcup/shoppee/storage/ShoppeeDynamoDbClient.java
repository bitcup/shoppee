package com.bitcup.shoppee.storage;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * @author bitcup
 */
public class ShoppeeDynamoDbClient {
    private final AmazonDynamoDBClient dynamoDBClient = AmazonDynamoDbClientHolder.getInstance();

    // Loads an item from DynamoDB by primary Hash Key. Callers of this method should pass in an
    // object which represents an item in the DynamoDB table item with the primary key populated
    public ShoppeeUserDataItem loadItem(final ShoppeeUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        return mapper.load(tableItem);
    }

    // Stores an item in DynamoDB
    public void saveItem(final ShoppeeUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        mapper.save(tableItem);
    }

    // Deletes an item from DynamoDB
    public void deleteItem(final ShoppeeUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        mapper.delete(tableItem);
    }

    // Creates a {@link DynamoDBMapper} using the default configurations.
    private DynamoDBMapper createDynamoDBMapper() {
        return new DynamoDBMapper(dynamoDBClient);
    }

}
