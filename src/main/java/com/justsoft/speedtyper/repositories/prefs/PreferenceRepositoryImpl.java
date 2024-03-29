package com.justsoft.speedtyper.repositories.prefs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

class PreferenceRepositoryImpl implements PreferenceRepository {
    private static final String preferenceNodePath = PreferenceRepositoryImpl.class.getPackageName().replace("\\.", "/");
    private final Preferences preferences = Preferences.userRoot().node(preferenceNodePath);

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
    public String getString(String key) {
        return this.preferences.get(key, null);
    }

    @Override
    public void setString(String key, String value) {
        this.preferences.put(key, value);
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

        this.preferences.put(key, formattedDate);
    }
}
