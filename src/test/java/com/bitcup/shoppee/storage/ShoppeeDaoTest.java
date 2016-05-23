package com.bitcup.shoppee.storage;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.User;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author bitcup
 */
public class ShoppeeDaoTest {

    final static String COSTCO = "costco";
    final static String STOP_AND_SHOP = "stop and shop";

    ShoppeeList list;
    private ShoppeeDao dao;
    private Session session;

    @Before
    public void setup() throws Exception {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/testCredz.properties");
        prop.load(in);
        String accessKey = prop.getProperty("accessKey");
        String secretKey = prop.getProperty("secretKey");
        in.close();
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentials);
        ShoppeeDynamoDbClient client = new ShoppeeDynamoDbClient(dynamoDBClient);
        dao = new ShoppeeDao(client);

        User user = User.builder().withUserId("testUser").build();
        session = Session.builder().withUser(user).withSessionId(UUID.randomUUID().toString()).build();
    }

    @After
    public void teardown() {
        if (list != null) {
            dao.deleteShoppeeList(list);
        }
    }

    @Test
    public void testIntegration() throws Exception {
        list = dao.getShoppeeList(session);
        assertNull("list is null", list);

        list = new ShoppeeList(session, new ShoppeeListData());
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertNotNull("list is not null", list);
        assertTrue(list.isEmpty());

        list.addItemToStore("cheese", COSTCO);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertEquals(1, list.getNumberOfItems(COSTCO));
        assertEquals(Lists.newArrayList("cheese"), list.getItemsForStore(COSTCO));

        list.addItemToStore("milk", STOP_AND_SHOP);
        list.addItemToStore("bread", STOP_AND_SHOP);
        list.addItemToStore("juice", STOP_AND_SHOP);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertEquals(3, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(Lists.newArrayList("milk", "bread", "juice"), list.getItemsForStore(STOP_AND_SHOP));

        list.removeItemFromStore("bread", STOP_AND_SHOP);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertEquals(2, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(Lists.newArrayList("milk", "juice"), list.getItemsForStore(STOP_AND_SHOP));

        list.clearItemsForStore(STOP_AND_SHOP);
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertEquals(0, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(1, list.getNumberOfItems(COSTCO));

        list.clearItemsForAllStores();
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertEquals(0, list.getNumberOfItems(STOP_AND_SHOP));
        assertEquals(0, list.getNumberOfItems(COSTCO));

        list.clearAll();
        dao.saveShoppeeList(list);
        list = dao.getShoppeeList(session);
        assertTrue(list.isEmpty());
    }
}
