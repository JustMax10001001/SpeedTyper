package com.justsoft.speedtyper.util;

import java.util.HashMap;
import java.util.Map;

public class Bundle {

    private final Map<String, Object> values = new HashMap<>();

    public void set(String key, Object value) {
        values.put(key, value);
    }

    public void setString(String key, String value) {
        values.put(key, value);
    }

    public void setInt(String key, int value) {
        values.put(key, value);
    }

    public void setFloat(String key, float value) {
        values.put(key, value);
    }

    public void setDouble(String key, double value) {
        values.put(key, value);
    }

    public void setBoolean(String key, boolean value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public int getInt(String key) {
        return (int) values.get(key);
    }

    public float getFloat(String key) {
        return (float) values.get(key);
    }

    public double getDouble(String key) {
        return (double) values.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) values.get(key);
    }

    public String getString(String key) {
        return (String) values.get(key);
    }
}
