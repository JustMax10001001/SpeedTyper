package com.justsoft.speedtyper.repositories.prefs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

class PreferenceRepositoryImpl extends PreferenceRepository {
    private static final String preferenceNodePath = PreferenceRepositoryImpl.class.getPackageName().replace("\\.", "/");
    private final Preferences preferences = Preferences.userRoot().node(preferenceNodePath);

    private static volatile PreferenceRepository instance;
    private static final Object instanceLock = new Object();

    public static PreferenceRepository getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new PreferenceRepositoryImpl();
                }
            }
        }

        return instance;
    }

    @Override
    public Integer getInt(String key) {
        var stringValue = getString(key);

        if (stringValue == null) {
            return null;
        }

        return Integer.parseInt(stringValue);
    }

    @Override
    public void setInt(String key, int value) {
        this.preferences.putInt(key, value);
    }

    @Override
    public LocalDate getDate(String key) {
        var stringValue = getString(key);

        if (stringValue == null) {
            return null;
        }

        return LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public void setDate(String key, LocalDate date) {
        var formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        this.setString(key, formattedDate);
    }

    private String getString(String key) {
        return this.preferences.get(key, null);
    }

    private void setString(String key, String value) {
        this.preferences.put(key, value);
    }
}
