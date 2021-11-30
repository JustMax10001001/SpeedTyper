package com.justsoft.speedtyper.repositories.prefs;

import java.time.LocalDate;

public abstract class PreferenceRepository {

    public abstract Integer getInt(String key);

    public abstract void setInt(String key, int value);

    public abstract LocalDate getDate(String key);

    public abstract void setDate(String key, LocalDate date);

    public static PreferenceRepository getInstance() {
        return PreferenceRepositoryImpl.getInstance();
    }
}
