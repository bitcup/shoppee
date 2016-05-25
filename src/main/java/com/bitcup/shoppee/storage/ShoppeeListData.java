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
    private final Map<String, List<ShoppeeListItem>> itemsByStore = Maps.newHashMap();

    public ShoppeeListData() {
    }

    public Map<String, List<ShoppeeListItem>> getItemsByStore() {
        return itemsByStore;
    }

    public void addItemToStore(String item, String store) {
        if (!itemsByStore.containsKey(store)) {
            itemsByStore.put(store, new ArrayList<>());
        }
        itemsByStore.get(store).add(new ShoppeeListItem(item));
    }

    public void removeItemFromStore(String item, String store) {
        if (!itemsByStore.containsKey(store)) {
            return;
        }
        Iterator<ShoppeeListItem> iter = itemsByStore.get(store).iterator();
        while (iter.hasNext()) {
            ShoppeeListItem sli = iter.next();
            if (sli.getName().equals(item)) {
                iter.remove();
                break;
            }
        }
    }

    public boolean storeContains(String item, String store) {
        if (!itemsByStore.containsKey(store)) {
            return false;
        }
        for (ShoppeeListItem sli : itemsByStore.get(store)) {
            if (sli.getName().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public void clearItemsForStore(String store) {
        if (!itemsByStore.containsKey(store)) {
            return;
        }
        itemsByStore.get(store).clear();
    }

    public void clearItemsForAllStores() {
        for (String store : itemsByStore.keySet()) {
            itemsByStore.get(store).clear();
        }
    }

    public void clearAll() {
        itemsByStore.clear();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("itemsByStore", itemsByStore)
                .toString();
    }
}
