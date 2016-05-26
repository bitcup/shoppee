package com.bitcup.shoppee.storage;

import com.amazon.speech.speechlet.Session;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bitcup
 */
public class ShoppeeList {
    private final Session session;
    private final ShoppeeListData listData;

    public ShoppeeList(Session session, ShoppeeListData listData) {
        this.session = session;
        this.listData = listData;
    }

    public Session getSession() {
        return session;
    }

    public ShoppeeListData getListData() {
        return listData;
    }

    public boolean isEmpty() {
        return listData.getItemsByStore().isEmpty();
    }

    public Set<String> getStores() {
        return listData.getItemsByStore().keySet().stream().map(ShoppeeStore::getName).collect(Collectors.toSet());
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
