package com.bitcup.shoppee.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * DynamoDB model
 *
 * @author bitcup
 */
@DynamoDBTable(tableName = "ShoppeeUserData")
public class ShoppeeUserDataItem {

    private String customerId;
    private ShoppeeListData listData;

    @DynamoDBHashKey(attributeName = "CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = ShoppeeListDataMarshaller.class)
    public ShoppeeListData getListData() {
        return listData;
    }

    public void setListData(ShoppeeListData listData) {
        this.listData = listData;
    }
}