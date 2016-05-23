package com.bitcup.shoppee.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DynamoDB model
 *
 * @author bitcup
 */
@DynamoDBTable(tableName = "ShoppeeUserData")
public class ShoppeeUserDataItem {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    // Provides marshalling and unmarshalling logic for ShoppeeListData values persistence in DynamoDB as a string
    public static class ShoppeeListDataMarshaller implements DynamoDBMarshaller<ShoppeeListData> {
        @Override
        public String marshall(ShoppeeListData listData) {
            try {
                return OBJECT_MAPPER.writeValueAsString(listData);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Unable to marshall list data", e);
            }
        }

        @Override
        public ShoppeeListData unmarshall(Class<ShoppeeListData> clazz, String value) {
            try {
                return OBJECT_MAPPER.readValue(value, new TypeReference<ShoppeeListData>() {
                });
            } catch (Exception e) {
                throw new IllegalStateException("Unable to unmarshall list data value", e);
            }
        }
    }
}