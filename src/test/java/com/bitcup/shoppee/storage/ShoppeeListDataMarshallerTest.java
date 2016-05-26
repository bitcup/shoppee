package com.bitcup.shoppee.storage;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author bitcup
 */
public class ShoppeeListDataMarshallerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppeeListDataMarshallerTest.class);

    final static String COSTCO = "costco";
    final static String STOP_AND_SHOP = "stop and shop";

    @Test
    public void testSerDeser() throws Exception {

        ShoppeeListData data = new ShoppeeListData();
        data.addItemToStore("milk", COSTCO);
        data.addItemToStore("coffee", STOP_AND_SHOP);
        data.addItemToStore("bread", COSTCO);
        data.addItemToStore("butter", COSTCO);
        LOGGER.info("data = {}", data);

        ShoppeeListDataMarshaller marshaller = new ShoppeeListDataMarshaller();
        String dataString = marshaller.marshall(data);
        LOGGER.info("dataString = {}", dataString);
        assertNotNull("dataString not null", dataString);

        ShoppeeListData fromString = marshaller.unmarshall(ShoppeeListData.class, dataString);
        LOGGER.info("fromString = {}", fromString);
        assertNotNull("fromString not null", fromString);
        final Map<ShoppeeStore, List<ShoppeeListItem>> itemsByStore = fromString.getItemsByStore();
        assertEquals(2, itemsByStore.size());
        final Set<String> storeNames = itemsByStore.keySet().stream().map(ShoppeeStore::getName).collect(Collectors.toSet());
        LOGGER.info("storeNames = {}", storeNames);
        assertTrue(storeNames.contains(COSTCO));
        assertTrue(storeNames.contains(STOP_AND_SHOP));

        ShoppeeStore costcoStore = fromString.getStoreByName(COSTCO);
        assertEquals(3, itemsByStore.get(costcoStore).size());
        final Set<String> costcoItems = itemsByStore.get(costcoStore).stream().map(ShoppeeListItem::getName).collect(Collectors.toSet());
        LOGGER.info("costcoItems = {}", costcoItems);
        assertTrue(costcoItems.contains("milk"));
        assertTrue(costcoItems.contains("bread"));
        assertTrue(costcoItems.contains("butter"));

        ShoppeeStore ssStore = fromString.getStoreByName(STOP_AND_SHOP);
        assertEquals(1, itemsByStore.get(ssStore).size());
        final Set<String> ssItems = itemsByStore.get(ssStore).stream().map(ShoppeeListItem::getName).collect(Collectors.toSet());
        LOGGER.info("ssItems = {}", ssItems);
        assertTrue(ssItems.contains("coffee"));
    }
}
