package com.justsoft.speedtyper.util;

public class Objects {

    public static <T> T notNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> T notNull(T value) {
        if (value == null) {
            throw new RuntimeException("Null check failed");
        }

        return value;
    }
}
