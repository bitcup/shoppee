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

    private static final String BRIEF_HELP_TEXT = "You can add an item to a store, remove an item from a store, or say help. What would you like?";

    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        init();
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        return getAskSpeechletResponse(BRIEF_HELP_TEXT, BRIEF_HELP_TEXT);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        init();
        Intent intent = request.getIntent();
        if ("AddItemToStoreIntent".equals(intent.getName())) {
            return getAddItemToStoreIntentResponse(intent, session);
        } else if ("RemoveItemFromStoreIntent".equals(intent.getName())) {
            return getRemoveItemFromStoreIntentResponse(intent, session);
        } else if ("ReadItemsFromStoreIntent".equals(intent.getName())) {
            return getReadItemsFromStoreIntentResponse(intent, session);
        } else if ("AMAZON.HelpIntent".equals(intent.getName())) {
            return getHelpIntentResponse();
        } else if ("AMAZON.CancelIntent".equals(intent.getName())) {
            return getExitIntentResponse();
        } else if ("AMAZON.StopIntent".equals(intent.getName())) {
            return getExitIntentResponse();
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

    private SpeechletResponse getAddItemToStoreIntentResponse(Intent intent, Session session) {
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
        // todo: what if item exists, but is already marked as purchased?
        // todo: prompt for "add another item?"
        if (list.storeContains(itemName, storeName)) {
            String speechText = itemName + " is already in " + storeName + " list.";
            return getAskSpeechletResponse(speechText, speechText);
        }
        list.addItemToStore(itemName, storeName);
        shoppeeDao.saveShoppeeList(list);
        String speechText = itemName + " added to " + storeName + " list";
        return getAskSpeechletResponse(speechText, BRIEF_HELP_TEXT);
    }

    private SpeechletResponse getRemoveItemFromStoreIntentResponse(Intent intent, Session session) {
        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null || list.isEmpty()) {
            String speechText = "You don't have any lists.";
            return getAskSpeechletResponse(speechText, speechText);
        }
        String storeName = intent.getSlot("StoreName").getValue();
        if (storeName == null) {
            String speechText = "Sorry, I did not hear the store name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        if (list.getNumberOfItems(storeName) == 0) {
            String speechText = "You don't have items in " + storeName + " list.";
            return getAskSpeechletResponse(speechText, speechText);
        }
        String itemName = intent.getSlot("ItemName").getValue();
        if (itemName == null) {
            String speechText = "Sorry, I did not hear the item name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        list.removeItemFromStore(itemName, storeName);
        shoppeeDao.saveShoppeeList(list);

        String speechText = itemName + " removed from " + storeName + " list.";
        return getAskSpeechletResponse(speechText, BRIEF_HELP_TEXT);
    }

    private SpeechletResponse getReadItemsFromStoreIntentResponse(Intent intent, Session session) {
        ShoppeeList list = shoppeeDao.getShoppeeList(session);
        if (list == null || list.isEmpty()) {
            String speechText = "You don't have any lists.";
            return getAskSpeechletResponse(speechText, speechText);
        }
        String storeName = intent.getSlot("StoreName").getValue();
        if (storeName == null) {
            String speechText = "Sorry, I did not hear the store name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        List<String> items = list.getItemsForStore(storeName);
        if (items.size() == 0) {
            String speechText = "You don't have items in " + storeName + " list.";
            return getAskSpeechletResponse(speechText, speechText);
        }
        String speechText = "List " + storeName + " contains: ";
        for (String item : items) {
            speechText += item + ", ";
        }
        return getAskSpeechletResponse(speechText, BRIEF_HELP_TEXT);
    }

    public SpeechletResponse getHelpIntentResponse() {
        String speechText = "Here are some things you can say: " +
                "Add milk to stop and shop, " +
                "remove cheese from costco, " +
                "read costco list. " +
                "You can also say stop if you’re done. " +
                "So, how can I help?";
        return getAskSpeechletResponse(speechText, BRIEF_HELP_TEXT);
    }

    public SpeechletResponse getExitIntentResponse() {
        String speechText = "Happy shopping!";
        return getTellSpeechletResponse(speechText);
    }

    private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Shoppee Session");
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
        card.setTitle("Shoppee Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

}
