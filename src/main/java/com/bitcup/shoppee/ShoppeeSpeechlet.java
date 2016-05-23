package com.bitcup.shoppee;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.bitcup.shoppee.storage.ShoppeeDynamoDbClient;
import com.bitcup.shoppee.storage.ShoppeeDao;
import com.bitcup.shoppee.storage.ShoppeeList;
import com.bitcup.shoppee.storage.ShoppeeListData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author bitcup
 */
public class ShoppeeSpeechlet implements Speechlet {

    private AmazonDynamoDBClient amazonDynamoDBClient;
    private ShoppeeDao shoppeeDao;

    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        init();
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        String speechText, repromptText;

        // get list summary by store
        ShoppeeList list = shoppeeDao.getShoppeeList(session);

        if (list == null || list.isEmpty()) {
            speechText = "You don't have items in your list.  Let's add some. What item do you want to add to which store?";
            repromptText = "Please tell me what item you want to add to which store?";
        } else {
            speechText = "";
            for (String store : list.getStores()) {
                speechText += "You have " + list.getNumberOfItems(store) + "items in " + store + " list.";
            }
            repromptText = "Here are things you can say.  Read me the Costco list. Add milk to Costco list. Remove milk from Costco list.";
        }
        return getAskSpeechletResponse(speechText, repromptText);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        init();
        Intent intent = request.getIntent();
        if ("AddItemToStoreIntent".equals(intent.getName())) {
            return getAddItemToStoreIntentResponse(intent, session);
        } else if ("RemoveItemFromStoreIntent".equals(intent.getName())) {
            return getRemoveItemFromStoreIntentResponse(intent, session);
        } else if ("ReadItemFromStoreIntent".equals(intent.getName())) {
            return getReadItemFromStoreIntentResponse(intent, session);
        } else if ("ClearItemsForStoreIntent".equals(intent.getName())) {
            return getClearItemsForStoreIntentResponse(intent, session);
        } else if ("ClearItemsForAllStoresIntent".equals(intent.getName())) {
            return getClearItemsForAllStoresIntentResponse(session);
        } else {
            throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
        // nothing to do
    }

    private void init() throws SpeechletException {
        if (amazonDynamoDBClient == null) {
            try {
                Properties prop = new Properties();
                InputStream in = getClass().getResourceAsStream("/credz.properties");
                prop.load(in);
                String accessKey = prop.getProperty("accessKey");
                String secretKey = prop.getProperty("secretKey");
                in.close();
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                amazonDynamoDBClient = new AmazonDynamoDBClient(credentials);
                ShoppeeDynamoDbClient client = new ShoppeeDynamoDbClient(amazonDynamoDBClient);
                shoppeeDao = new ShoppeeDao(client);
            } catch (IOException e) {
                throw new SpeechletException(e);
            }
        }
    }

    public SpeechletResponse getAddItemToStoreIntentResponse(Intent intent, Session session) {
        String storeName = intent.getSlot("StoreName").getValue();
        if (storeName == null) {
            String speechText = "Sorry, I did not hear the store name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        String itemName = intent.getSlot("ItemName").getValue();
        if (itemName == null) {
            String speechText = "Sorry, I did not hear the item name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null) {
            list = new ShoppeeList(session, new ShoppeeListData());
        }
        list.addItemToStore(itemName, storeName);
        shoppeeDao.saveShoppeeList(list);

        String speechText = "Item " + itemName + " added to " + storeName + " list";

        return getTellSpeechletResponse(speechText);
    }

    public SpeechletResponse getRemoveItemFromStoreIntentResponse(Intent intent, Session session) {
        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null || list.isEmpty()) {
            String speechText = "You don't have any lists.";
            return getTellSpeechletResponse(speechText);
        }
        String storeName = intent.getSlot("StoreName").getValue();
        if (storeName == null) {
            String speechText = "Sorry, I did not hear the store name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        if (list.getNumberOfItems(storeName) == 0) {
            String speechText = "You don't have items in " + storeName + " list.";
            return getTellSpeechletResponse(speechText);
        }
        String itemName = intent.getSlot("ItemName").getValue();
        if (itemName == null) {
            String speechText = "Sorry, I did not hear the item name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        list.removeItemFromStore(itemName, storeName);
        shoppeeDao.saveShoppeeList(list);

        String speechText = "Item " + itemName + " removed from " + storeName + " list.";
        return getTellSpeechletResponse(speechText);
    }

    public SpeechletResponse getReadItemFromStoreIntentResponse(Intent intent, Session session) {
        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null || list.isEmpty()) {
            String speechText = "You don't have any lists.";
            return getTellSpeechletResponse(speechText);
        }
        String storeName = intent.getSlot("StoreName").getValue();
        if (storeName == null) {
            String speechText = "Sorry, I did not hear the store name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        List<String> items = list.getItemsForStore(storeName);
        if (items.size() == 0) {
            String speechText = "You don't have items in " + storeName + " list.";
            return getTellSpeechletResponse(speechText);
        }
        String speechText = "List " + storeName + " contains: ";
        for (String item : items) {
            speechText += item + ", ";
        }
        return getTellSpeechletResponse(speechText);
    }

    public SpeechletResponse getClearItemsForStoreIntentResponse(Intent intent, Session session) {
        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null || list.isEmpty()) {
            String speechText = "You don't have any lists.";
            return getTellSpeechletResponse(speechText);
        }
        String storeName = intent.getSlot("StoreName").getValue();
        if (storeName == null) {
            String speechText = "Sorry, I did not hear the store name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        List<String> items = list.getItemsForStore(storeName);
        if (items.size() == 0) {
            String speechText = "You don't have items in " + storeName + " list.";
            return getTellSpeechletResponse(speechText);
        }
        list.clearItemsForStore(storeName);
        shoppeeDao.saveShoppeeList(list);
        String speechText = "List " + storeName + " cleared.";
        return getTellSpeechletResponse(speechText);
    }

    public SpeechletResponse getClearItemsForAllStoresIntentResponse(Session session) {
        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null || list.isEmpty()) {
            String speechText = "You don't have any lists.";
            return getTellSpeechletResponse(speechText);
        }
        list.clearItemsForAllStores();
        shoppeeDao.saveShoppeeList(list);
        String speechText = "All lists cleared.";
        return getTellSpeechletResponse(speechText);
    }

    private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getTellSpeechletResponse(String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

}
