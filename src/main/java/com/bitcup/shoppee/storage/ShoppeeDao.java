package com.bitcup.shoppee.storage;

import com.amazon.speech.speechlet.Session;

/**
 * @author bitcup
 */
public class ShoppeeDao {
    private final ShoppeeDynamoDbClient dynamoDbClient;

    public ShoppeeDao(ShoppeeDynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public ShoppeeList getShoppeeList(Session session) {
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setCustomerId(session.getUser().getUserId());
        item = dynamoDbClient.loadItem(item);
        if (item == null) {
            return null;
        }
        return new ShoppeeList(session, item.getListData());
    }

    public void saveShoppeeList(ShoppeeList list) {
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setCustomerId(list.getSession().getUser().getUserId());
        item.setListData(list.getListData());
        dynamoDbClient.saveItem(item);
    }

    public void deleteShoppeeList(ShoppeeList list) {
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setCustomerId(list.getSession().getUser().getUserId());
        item.setListData(list.getListData());
        dynamoDbClient.deleteItem(item);
    }
}
