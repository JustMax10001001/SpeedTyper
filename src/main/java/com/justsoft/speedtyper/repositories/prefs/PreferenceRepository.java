package com.justsoft.speedtyper.repositories.prefs;

import java.time.LocalDate;

public interface PreferenceRepository {

    Integer getInt(String key);

    void setInt(String key, int value);

    String getString(String key);

    void setString(String key, String value);

    LocalDate getDate(String key);

    void setDate(String key, LocalDate date);

    PreferenceRepository instance = new PreferenceRepositoryImpl();

    static PreferenceRepository getInstance() {
        return instance;
    }
}
