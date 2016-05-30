package com.bitcup.shoppee.alexa;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.bitcup.shoppee.utils.Props;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bitcup
 */
public class ShoppeeSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> SUPPORTED_APPLICATION_IDS;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        SUPPORTED_APPLICATION_IDS = new HashSet<>();
        Props props = new Props("credz.properties");
        String supportedAppId = props.getString("supportedAppId");
        SUPPORTED_APPLICATION_IDS.add(supportedAppId);
    }

    public ShoppeeSpeechletRequestStreamHandler() {
        super(new ShoppeeSpeechlet(), SUPPORTED_APPLICATION_IDS);
    }
}
