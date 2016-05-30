package com.bitcup.shoppee.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * Provides marshalling and unmarshalling logic for ShoppeeListData values persistence in DynamoDB as a string.
 * JSON does not support objects (ShoppeeStore) as map keys, so we use ShoppeeStoreSerializer to properly serialize the name property of the key.
 *
 * @author bitcup
 */
public class ShoppeeListDataMarshaller implements DynamoDBMarshaller<ShoppeeListData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppeeListDataMarshaller.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        module.addKeySerializer(ShoppeeStore.class, new ShoppeeStoreSerializer());
        MapType myMapType = TypeFactory.defaultInstance().constructMapType(HashMap.class, ShoppeeStore.class, String.class);
        OBJECT_MAPPER.registerModule(module).writerWithType(myMapType);
        LOGGER.info("registered ShoppeeStoreSerializer");
    }

    @Override
    public String marshall(ShoppeeListData listData) {
        try {
            LOGGER.info("from: {}", this);
            return OBJECT_MAPPER.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to marshall list data", e);
        }
    }

    @Override
    public ShoppeeListData unmarshall(Class<ShoppeeListData> clazz, String value) {
        try {
            return OBJECT_MAPPER.readValue(value, new TypeReference<ShoppeeListData>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Unable to unmarshall list data value", e);
        }
    }

    public static class ShoppeeStoreSerializer extends JsonSerializer<ShoppeeStore> {
        @Override
        public void serialize(ShoppeeStore value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            jsonGenerator.writeFieldName(value.getName());
        }
    }
}
