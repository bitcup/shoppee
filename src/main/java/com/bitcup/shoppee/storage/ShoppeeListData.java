package com.bitcup.shoppee.storage;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bitcup
 */
public class ShoppeeListData {
    private final Map<String, List<String>> itemsByStore = Maps.newHashMap();

    public ShoppeeListData() {
    }

    public Map<String, List<String>> getItemsByStore() {
        return itemsByStore;
    }

    public void addItemToStore(String item, String store) {
        if (!itemsByStore.containsKey(store)) {
            itemsByStore.put(store, new ArrayList<String>());
        }
        itemsByStore.get(store).add(item);
    }

    public void removeItemFromStore(String item, String store) {
        if (!itemsByStore.containsKey(store)) {
            return;
        }
        itemsByStore.get(store).remove(item);
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
