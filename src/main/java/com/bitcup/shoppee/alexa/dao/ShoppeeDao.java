package com.bitcup.shoppee.alexa.dao;

import com.bitcup.shoppee.alexa.dto.ShoppeeList;
import com.bitcup.shoppee.storage.ShoppeeDynamoDbClient;
import com.bitcup.shoppee.storage.ShoppeeUserDataItem;

/**
 * @author bitcup
 */
public class ShoppeeDao {

    private final ShoppeeDynamoDbClient dynamoDbClient = new ShoppeeDynamoDbClient();

    public ShoppeeList getShoppeeList(String userId) {
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setUserId(userId);
        item = dynamoDbClient.loadItem(item);
        if (item == null) {
            return null;
        }
        return new ShoppeeList(userId, item.getListData());
    }

    public void saveShoppeeList(ShoppeeList list) {
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setUserId(list.getUserId());
        item.setListData(list.getListData());
        dynamoDbClient.saveItem(item);
    }

    public void deleteShoppeeList(ShoppeeList list) {
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setUserId(list.getUserId());
        item.setListData(list.getListData());
        dynamoDbClient.deleteItem(item);
    }
}
