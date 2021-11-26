package com.justsoft.speedtyper.services.prefs;

import com.justsoft.speedtyper.repositories.prefs.PreferenceRepository;

import java.time.LocalDate;

public interface PreferenceService {
    int sessionTime();

    void setSessionTime(int sessionTimeSeconds);

    LocalDate resultsNotBeforeTime();

    void setResultsNotBeforeTime(LocalDate resultsNotBeforeTime);

    LocalDate resultsNotAfterTime();

    void setResultsNotAfterTime(LocalDate resultsNotAfterTime);

    PreferenceService instance = new PreferenceServiceImpl(PreferenceRepository.getInstance());

    static PreferenceService getInstance() {
        return instance;
    }
}
