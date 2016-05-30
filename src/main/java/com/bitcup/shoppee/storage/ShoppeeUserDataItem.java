package com.bitcup.shoppee.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.bitcup.shoppee.model.ShoppeeListData;
import com.bitcup.shoppee.model.ShoppeeListDataMarshaller;

/**
 * DynamoDB model
 *
 * @author bitcup
 */
@DynamoDBTable(tableName = "ShoppeeUserData")
public class ShoppeeUserDataItem {

    private String userId;
    private ShoppeeListData listData;

    @DynamoDBHashKey(attributeName = "CustomerId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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