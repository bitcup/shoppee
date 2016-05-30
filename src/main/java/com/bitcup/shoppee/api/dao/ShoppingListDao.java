package com.bitcup.shoppee.api.dao;

import com.bitcup.shoppee.alexa.dto.ShoppeeList;
import com.bitcup.shoppee.api.dto.ShoppingItemDto;
import com.bitcup.shoppee.api.dto.ShoppingListDto;
import com.bitcup.shoppee.model.ShoppeeListData;
import com.bitcup.shoppee.model.ShoppeeListItem;
import com.bitcup.shoppee.model.ShoppeeStore;
import com.bitcup.shoppee.storage.ShoppeeDynamoDbClient;
import com.bitcup.shoppee.storage.ShoppeeUserDataItem;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author bitcup
 */
public class ShoppingListDao {

    private final ShoppeeDynamoDbClient dynamoDbClient = new ShoppeeDynamoDbClient();

    public List<ShoppingListDto> getShoppingLists(String userId) {
        List<ShoppingListDto> dtoList = Lists.newArrayList();
        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
        item.setUserId(userId);
        item = dynamoDbClient.loadItem(item);
        if (item == null) {
            return dtoList;
        }
        ShoppeeListData shoppeeListData = item.getListData();
        for(Map.Entry<ShoppeeStore, List<ShoppeeListItem>> entry : shoppeeListData.getItemsByStore().entrySet()) {
            ShoppingListDto dto = new ShoppingListDto();
            dto.setId(entry.getKey().getId());
            dto.setName(entry.getKey().getName());
            List<ShoppingItemDto> itemDtos = Lists.newArrayList();
            for (ShoppeeListItem sli : entry.getValue()) {
                ShoppingItemDto itemDto = new ShoppingItemDto();
                itemDto.setId(sli.getId());
                itemDto.setName(sli.getName());
                itemDto.setPurchased(sli.isPurchased());
            }
            dto.setItems(itemDtos);
        }
        return dtoList;
    }

//    public void saveShoppeeList(ShoppeeList list) {
//        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
//        item.setUserId(list.getUserId());
//        item.setListData(list.getListData());
//        dynamoDbClient.saveItem(item);
//    }
//
//    public void deleteShoppeeList(ShoppeeList list) {
//        ShoppeeUserDataItem item = new ShoppeeUserDataItem();
//        item.setUserId(list.getUserId());
//        item.setListData(list.getListData());
//        dynamoDbClient.deleteItem(item);
//    }

}
