package com.bitcup.shoppee.storage;

import com.amazon.speech.speechlet.User;
import com.bitcup.shoppee.alexa.dao.ShoppeeDao;
import com.bitcup.shoppee.alexa.dto.ShoppeeList;
import com.bitcup.shoppee.model.ShoppeeListData;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author bitcup
 */
public class ShoppeeDaoTest {

    final static String COSTCO = "costco";
    final static String STOP_AND_SHOP = "stop and shop";

    ShoppeeList list;
    private ShoppeeDao dao;
    private String userId;

    @Before
    public void setup() throws Exception {
        dao = new ShoppeeDao();
        User user = User.builder().withUserId("testUser").build();
        userId = user.getUserId();
    }

    @After
    public void teardown() {
        if (list != null) {
            dao.deleteShoppeeList(list);
        }
    }

    @Test
    public void testIntegration() throws Exception {
        list = dao.getShoppeeList(userId);
        assertNull("list is null", list);

        list = new ShoppeeList(userId, new ShoppeeListData());
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertNotNull("list is not null", list);
        assertTrue(list.isEmpty());

        list.addItemToStore("cheese", COSTCO);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertEquals(1, list.getNumberOfItems(COSTCO));
        assertEquals(Lists.newArrayList("cheese"), list.getItemsForStore(COSTCO));

        list.addItemToStore("milk", STOP_AND_SHOP);
        list.addItemToStore("bread", STOP_AND_SHOP);
        list.addItemToStore("juice", STOP_AND_SHOP);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertEquals(3, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(Lists.newArrayList("milk", "bread", "juice"), list.getItemsForStore(STOP_AND_SHOP));

        list.removeItemFromStore("bread", STOP_AND_SHOP);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertEquals(2, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(Lists.newArrayList("milk", "juice"), list.getItemsForStore(STOP_AND_SHOP));

        list.clearItemsForStore(STOP_AND_SHOP);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertEquals(0, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(1, list.getNumberOfItems(COSTCO));

        list.clearItemsForAllStores();
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertEquals(0, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(0, list.getNumberOfItems(COSTCO));

        list.clearAll();
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(userId);
        assertTrue(list.isEmpty());
    }
}
