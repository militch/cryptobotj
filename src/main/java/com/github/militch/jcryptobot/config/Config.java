package com.github.militch.jcryptobot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties;

    public static Config load(String path) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(path);
        Properties p = new Properties();
        p.load(is);
        return new Config(p);
    }
    public Config(Properties properties) {
        this.properties = properties;
    }
    public String getString(String key, String defaultValue){
        String value = getString(key);
        return (value == null) ? defaultValue : value;
    }
    public String getString(String key){
        return properties.getProperty(key);
    }
    public Integer getInt(String key){
        String val = getString(key);
        if (val == null) {
            return null;
        }
        return Integer.parseInt(val);
    }
    public Integer getInt(String key, int defaultValue){
        Integer val = getInt(key);
        return (val == null) ? defaultValue : val;
    }
}
