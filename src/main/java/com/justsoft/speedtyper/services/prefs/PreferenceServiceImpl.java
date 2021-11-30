package com.justsoft.speedtyper.services.prefs;

import com.justsoft.speedtyper.repositories.prefs.PreferenceRepository;

import java.time.LocalDate;

import static com.justsoft.speedtyper.util.Objects.notNull;

class PreferenceServiceImpl extends PreferenceService {
    private static final String SESSION_TIME_KEY = "session_time";
    private static final String RESULT_NOT_BEFORE_KEY = "result_not_before";
    private static final String RESULT_NOT_AFTER_KEY = "result_not_after";

    private static final int DEFAULT_SESSION_TIME_SECONDS = 60;
    private static final int DEFAULT_NOT_BEFORE_DAYS_BEFORE_NOW = 7;

    private final PreferenceRepository preferenceRepository;

    private static volatile PreferenceServiceImpl instance;
    private static final Object instanceLock = new Object();

    public static PreferenceService getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new PreferenceServiceImpl(PreferenceRepository.getInstance());
                }
            }
        }

        return instance;
    }

    private PreferenceServiceImpl(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public int sessionTime() {
        return notNull(
                this.preferenceRepository.getInt(SESSION_TIME_KEY),
                DEFAULT_SESSION_TIME_SECONDS
        );
    }

    @Override
    public void setSessionTime(int sessionTimeSeconds) {
        this.preferenceRepository.setInt(SESSION_TIME_KEY, sessionTimeSeconds);
    }

    @Override
    public LocalDate resultsNotBeforeTime() {
        return notNull(
                this.preferenceRepository.getDate(RESULT_NOT_BEFORE_KEY),
                LocalDate.now().minusDays(DEFAULT_NOT_BEFORE_DAYS_BEFORE_NOW)
        );
    }

    @Override
    public void setResultsNotBeforeTime(LocalDate resultsNotBeforeTime) {
        this.preferenceRepository.setDate(RESULT_NOT_BEFORE_KEY, resultsNotBeforeTime);
    }

    @Override
    public LocalDate resultsNotAfterTime() {
        return LocalDate.now();
    }

    @Override
    public void setResultsNotAfterTime(LocalDate resultsNotAfterTime) {
        this.preferenceRepository.setDate(RESULT_NOT_AFTER_KEY, resultsNotAfterTime);
    }
}
