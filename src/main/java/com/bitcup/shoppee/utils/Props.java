package com.bitcup.shoppee.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author bitcup
 */
public class Props extends HashMap<String, String> {
    private static final long serialVersionUID = 2211405016738281987L;

    public Props() {
    }

    public Props(String url) {
        load(url);
        expandVariables();
    }

    public Props(URL url) {
        load(url);
        expandVariables();
    }

    public Props(Properties props) {
        fromProperties(props);
        expandVariables();
    }

    public Props(Map<String, String> map) {
        putAll(map);
        expandVariables();
    }

    public List<String> getGroupKeys(String groupKey) {
        List<String> result = new ArrayList<String>();
        for (String key : keySet()) {
            if (key.startsWith(groupKey))
                result.add(key);
        }
        return result;
    }

    public String getString(String key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Map key not found: " + key);
        }
        return get(key);
    }

    public String getString(String key, String def) {
        String result = get(key);
        if (result == null)
            result = def;
        return result;
    }

    public int getInt(String key) {
        String val = getString(key);
        return Integer.parseInt(val);
    }

    public int getInt(String key, int def) {
        String val = getString(key, "" + def);
        return Integer.parseInt(val);
    }

    public boolean getBoolean(String key) {
        String val = getString(key);
        return Boolean.parseBoolean(val);
    }

    public boolean getBoolean(String key, boolean def) {
        String val = getString(key, "" + def);
        return Boolean.parseBoolean(val);
    }

    public long getLong(String key) {
        String val = getString(key);
        return Long.parseLong(val);
    }

    public long getLong(String key, long def) {
        String val = getString(key, "" + def);
        return Long.parseLong(val);
    }

    public double getDouble(String key) {
        String val = getString(key);
        return Double.parseDouble(val);
    }

    public double getDouble(String key, double def) {
        String val = getString(key, "" + def);
        return Double.parseDouble(val);
    }

    public void load(String url) {
        URL urlObj = getURL(url);
        load(urlObj);
    }

    public void load(URL url) {
        try {
            load(url.openStream()); // load() should call close on stream.
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to open " + url, e);
        }
    }

    public void load(InputStream inStream) {
        Properties props = null;
        try {
            props = new Properties();
            props.load(inStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inStream != null) {
                IOUtils.closeQuietly(inStream);
            }
        }
        fromProperties(props);
    }

    /**
     * Auto expand any ${variable} in expandProps using a lookupProps for existing variable definitions. This method
     * will automatically search the System Properties space for lookup as well.
     * <p>
     * Note: There is no going back after you call this method!
     */
    public void expandVariables() {
        // We will allow System Properties for override when doing lookup.
        Props lookupProps = new Props();
        lookupProps.putAll(this);
        lookupProps.fromProperties(System.getProperties());

        StrSubstitutor substitutor = new StrSubstitutor(lookupProps);
        for (Map.Entry<String, String> entry : entrySet()) {
            String name = entry.getKey();
            String val = entry.getValue();
            if (val == null)
                continue;
            String newVal = substitutor.replace(val);
            if (!newVal.equals(val)) {
                put(name, newVal);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void fromProperties(Properties props) {
        Map<String, String> map = (Map) props;
        super.putAll(map);
    }

    /**
     * Clone this map and return it as java.util.Properties
     */
    @SuppressWarnings("unchecked")
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.putAll((Map<String, String>) this.clone());
        return properties;
    }

    private URL getURL(String url) {
        return this.getClass().getClassLoader().getResource(url);
    }
}
