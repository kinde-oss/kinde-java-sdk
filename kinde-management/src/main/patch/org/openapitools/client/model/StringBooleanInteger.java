package org.openapitools.client.model;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.13.0")
public class StringBooleanInteger extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(StringBooleanInteger.class.getName());

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!StringBooleanInteger.class.isAssignableFrom(type.getRawType())) {
                return null;
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<String> adapterString = gson.getDelegateAdapter(this, TypeToken.get(String.class));
            final TypeAdapter<Boolean> adapterBoolean = gson.getDelegateAdapter(this, TypeToken.get(Boolean.class));
            final TypeAdapter<Integer> adapterInteger = gson.getDelegateAdapter(this, TypeToken.get(Integer.class));

            return (TypeAdapter<T>) new TypeAdapter<StringBooleanInteger>() {
                @Override
                public void write(JsonWriter out, StringBooleanInteger value) throws IOException {
                    if (value == null || value.getActualInstance() == null) {
                        elementAdapter.write(out, null);
                        return;
                    }

                    Object actualInstance = value.getActualInstance();
                    if (actualInstance instanceof String) {
                        adapterString.write(out, (String) actualInstance);
                    } else if (actualInstance instanceof Boolean) {
                        adapterBoolean.write(out, (Boolean) actualInstance);
                    } else if (actualInstance instanceof Integer) {
                        adapterInteger.write(out, (Integer) actualInstance);
                    } else {
                        throw new IOException("Failed to serialize as the type doesn't match oneOf schemas: Boolean, Integer, String");
                    }
                }

                @Override
                public StringBooleanInteger read(JsonReader in) throws IOException {
                    Object deserialized = null;
                    JsonElement jsonElement = elementAdapter.read(in);

                    ArrayList<String> errorMessages = new ArrayList<>();
                    int match = 0;

                    // Attempt to deserialize as each type
                    try {
                        deserialized = adapterString.fromJsonTree(jsonElement);
                        match++;
                    } catch (Exception e) {
                        errorMessages.add(String.format("Deserialization for String failed with `%s`.", e.getMessage()));
                    }
                    try {
                        deserialized = adapterBoolean.fromJsonTree(jsonElement);
                        match++;
                    } catch (Exception e) {
                        errorMessages.add(String.format("Deserialization for Boolean failed with `%s`.", e.getMessage()));
                    }
                    try {
                        deserialized = adapterInteger.fromJsonTree(jsonElement);
                        match++;
                    } catch (Exception e) {
                        errorMessages.add(String.format("Deserialization for Integer failed with `%s`.", e.getMessage()));
                    }

                    if (match == 1) {
                        StringBooleanInteger ret = new StringBooleanInteger();
                        ret.setActualInstance(deserialized);
                        return ret;
                    }

                    throw new IOException(String.format("Failed deserialization for StringBooleanInteger: %d classes match result, expected 1. Detailed failure message for oneOf schemas: %s. JSON: %s", match, errorMessages, jsonElement.toString()));
                }
            }.nullSafe();
        }
    }

    public static final Map<String, Class<?>> schemas = new HashMap<>();

    static {
        schemas.put("String", String.class);
        schemas.put("Boolean", Boolean.class);
        schemas.put("Integer", Integer.class);
    }

    public StringBooleanInteger() {
        super("oneOf", Boolean.FALSE);
    }

    public StringBooleanInteger(Object o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return StringBooleanInteger.schemas;
    }

    @Override
    public void setActualInstance(Object instance) {
        if (instance instanceof String || instance instanceof Boolean || instance instanceof Integer) {
            super.setActualInstance(instance);
        } else {
            throw new RuntimeException("Invalid instance type. Must be String, Boolean, or Integer");
        }
    }
} 