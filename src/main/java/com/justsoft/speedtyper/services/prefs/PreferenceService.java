package com.justsoft.speedtyper.services.prefs;

import java.time.LocalDate;

public abstract class PreferenceService {
    public abstract int sessionTime();

    public abstract void setSessionTime(int sessionTimeSeconds);

    public abstract LocalDate resultsNotBeforeTime();

    public abstract void setResultsNotBeforeTime(LocalDate resultsNotBeforeTime);

    public abstract LocalDate resultsNotAfterTime();

    public abstract void setResultsNotAfterTime(LocalDate resultsNotAfterTime);

    public static PreferenceService getInstance() {
        return PreferenceServiceImpl.getInstance();
    }
}
