package com.bitcup.shoppee.alexa.dto;

import com.bitcup.shoppee.model.ShoppeeListData;
import com.bitcup.shoppee.model.ShoppeeListItem;
import com.bitcup.shoppee.model.ShoppeeStore;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO used by speechlet class
 *
 * @author bitcup
 */
public class ShoppeeList {
    private final String userId;
    private final ShoppeeListData listData;

    public ShoppeeList(String userId) {
        this.userId = userId;
        this.listData = new ShoppeeListData();
    }

    public ShoppeeList(String userId, ShoppeeListData listData) {
        this.userId = userId;
        this.listData = listData;
    }

    public String getUserId() {
        return userId;
    }

    public ShoppeeListData getListData() {
        return listData;
    }

    public boolean isEmpty() {
        return listData.getItemsByStore().isEmpty();
    }

    public Set<ShoppeeStore> getStores() {
        return listData.getItemsByStore().keySet();
    }

    public boolean storeContains(String item, String store) {
        return listData.storeContains(item, store);
    }

    public List<String> getItemsForStore(String store) {
        ShoppeeStore ss = listData.getStoreByName(store);
        if (ss != null) {
            return listData.getItemsByStore().get(ss).stream().map(ShoppeeListItem::getName).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public int getNumberOfItems(String store) {
        return getItemsForStore(store).size();
    }

    public void addItemToStore(String item, String store) {
        listData.addItemToStore(item, store);
    }

    public void removeItemFromStore(String item, String store) {
        listData.removeItemFromStore(item, store);
    }

    public void clearItemsForStore(String store) {
        listData.clearItemsForStore(store);
    }

    public void clearItemsForAllStores() {
        listData.clearItemsForAllStores();
    }

    public void clearAll() {
        listData.clearAll();
    }
}
