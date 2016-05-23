package com.bitcup.shoppee;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author bitcup
 */
public class ShoppeeSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds = new HashSet<>();
        try {
            Properties prop = new Properties();
            InputStream in = ShoppeeSpeechletRequestStreamHandler.class.getResourceAsStream("/credz.properties");
            prop.load(in);
            String supportedAppId = prop.getProperty("supportedAppId");
            in.close();
            supportedApplicationIds.add(supportedAppId);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ShoppeeSpeechletRequestStreamHandler() {
        super(new ShoppeeSpeechlet(), supportedApplicationIds);
    }
}
