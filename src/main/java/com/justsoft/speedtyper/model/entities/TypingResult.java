package com.justsoft.speedtyper.model.entities;

import java.time.LocalDate;

public record TypingResult(
        int id,
        int totalChars,
        int totalWords,
        int mistakenWords,
        LocalDate sessionDate,
        int sessionTimeSeconds
) implements BaseEntityRecord {
    public TypingResult(
            int totalChars,
            int totalWords,
            int mistakenWords,
            LocalDate sessionDate,
            int sessionTimeSeconds
    ) {
        this(0, totalChars, totalWords, mistakenWords, sessionDate, sessionTimeSeconds);
    }

    public TypingResult withId(int id) {
        return new TypingResult(id, this.totalChars, this.totalWords, this.mistakenWords, this.sessionDate, this.sessionTimeSeconds);
    }

    public double wordsPerMinute() {
        return ((double) this.totalWords) / this.sessionTimeSeconds * 60d;
    }

    public double charsPerMinute() {
        return ((double) this.totalChars) / this.sessionTimeSeconds * 60d;
    }
}