package com.bitcup.shoppee.storage;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author bitcup
 */
public class ShoppeeListData {
    private final Map<ShoppeeStore, List<ShoppeeListItem>> itemsByStore = Maps.newHashMap();

    public ShoppeeListData() {
    }

    public Map<ShoppeeStore, List<ShoppeeListItem>> getItemsByStore() {
        return itemsByStore;
    }

    public void addItemToStore(String item, String store) {
        ShoppeeStore storeByName = getStoreByName(store);
        if (storeByName == null) {
            storeByName = new ShoppeeStore(store);
            itemsByStore.put(storeByName, new ArrayList<>());
        }
        itemsByStore.get(storeByName).add(new ShoppeeListItem(item));
    }

    public void removeItemFromStore(String item, String store) {
        ShoppeeStore storeByName = getStoreByName(store);
        if (storeByName == null) {
            return;
        }
        Iterator<ShoppeeListItem> iterator = itemsByStore.get(storeByName).iterator();
        while (iterator.hasNext()) {
            ShoppeeListItem sli = iterator.next();
            if (sli.getName().equals(item)) {
                iterator.remove();
                break;
            }
        }
    }

    public boolean storeContains(String item, String store) {
        ShoppeeStore storeByName = getStoreByName(store);
        if (storeByName == null) {
            return false;
        }
        for (ShoppeeListItem sli : itemsByStore.get(storeByName)) {
            if (sli.getName().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public void clearItemsForStore(String store) {
        ShoppeeStore storeByName = getStoreByName(store);
        if (storeByName == null) {
            return;
        }
        itemsByStore.get(storeByName).clear();
    }

    public void clearItemsForAllStores() {
        for (ShoppeeStore store : itemsByStore.keySet()) {
            itemsByStore.get(store).clear();
        }
    }

    public void clearAll() {
        itemsByStore.clear();
    }

    public ShoppeeStore getStoreByName(String store) {
        for (ShoppeeStore ss : itemsByStore.keySet()) {
            if (ss.getName().equals(store)) {
                return ss;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("itemsByStore", itemsByStore)
                .toString();
    }
}
