package com.justsoft.speedtyper.model;

import java.time.LocalDate;

public record TypingSessionResult(
        int id,
        int totalChars,
        int totalWords,
        int mistakenWords,
        LocalDate sessionDate,
        int sessionTimeSeconds
) implements BaseEntityRecord {
    public TypingSessionResult(
            int totalChars,
            int totalWords,
            int mistakenWords,
            LocalDate sessionDate,
            int sessionTimeSeconds
    ) {
        this(0, totalChars, totalWords, mistakenWords, sessionDate, sessionTimeSeconds);
    }

    public TypingSessionResult withId(int id) {
        return new TypingSessionResult(id, totalChars, totalWords, mistakenWords, sessionDate, sessionTimeSeconds);
    }

    public double getWordsPerMinute() {
        return ((double) totalWords) / sessionTimeSeconds * 60d;
    }

    public double getCharsPerMinute() {
        return ((double) totalChars) / sessionTimeSeconds * 60d;
    }
}