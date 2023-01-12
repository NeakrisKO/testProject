package ru.itb.testautomation.core.common;

import org.apache.commons.lang3.StringUtils;


import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final String CONFIG_PATH = System.getProperty("config.file", "config.properties");
    private Properties PROPERTIES = new Properties();
    private static Config config;
    private final Logger LOGGER =  LogManager.getLogger(Config.class);

    public static Config getConfig() {
        if (config == null) {
            synchronized (Config.class) {
                if (config == null) {
                    config = new Config(true);
                }
            }
        }
        return config;
    }

    private Config(boolean withLoading) {
        if (withLoading) {
            PROPERTIES = new Properties();
            try (FileReader reader = new FileReader(CONFIG_PATH)) {
                PROPERTIES.load(reader);
            } catch (IOException e) {
                LOGGER.warn("Unable to load configuration file '{}'", CONFIG_PATH);
            }
        }
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return getKeyValue(key, defaultValue, String.class).trim();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return getKeyValue(key, defaultValue, Integer.class);
    }

    private <T> T getKeyValue(String key, Object defaultValue, Class<T> type) {
        Object value = PROPERTIES.get(key);
        if (value == null) {
            LOGGER.warn("Key '{}' not found in properties file '{}'", key, CONFIG_PATH);
            return type.cast(defaultValue);
        }
        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            //If user put string instead of int, for example
            LOGGER.error("Type mismatch: actual type '{}', expected '{}'. Key is '{}', value is '{}'", value.getClass().getSimpleName(), type.getSimpleName(), key, value);
            return type.cast(defaultValue);
        }
    }

    public void addProperty(String key, Object value) {
        PROPERTIES.put(key, value);
    }

    public void merge(Properties properties) {
        PROPERTIES.putAll(properties);
    }

    public Config getByPrefix(String prefix) {
        Config config = new Config(false);
        for (Map.Entry<Object, Object> property : PROPERTIES.entrySet()) {
            String key = property.getKey().toString();
            if (StringUtils.startsWith(key, prefix)) {
                config.addProperty(key.substring(prefix.length() + 1), property.getValue());
            }
        }
        return config;
    }

    public Collection<Object> getValues() {
        return getValues(Object.class);
    }

    public <T> Collection<T> getValues(Class<T> type) {
        Collection<Object> objects = PROPERTIES.values();
        LinkedList<T> list = new LinkedList<>();
        for (Object object : objects) {
            list.addFirst(type.cast(object));
        }
        return list;
    }
}
