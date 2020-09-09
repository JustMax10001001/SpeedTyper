package com.justsoft.speedtyper.model;

import java.time.LocalDate;

public class TypingSessionResult extends BaseModel {

    private int totalChars;
    private int totalWords;

    private int mistakenWords;
    private LocalDate sessionDate;

    private int sessionTimeSeconds;

    public double getWordsPerMinute() {
        return ((double) totalWords) / sessionTimeSeconds * 60d;
    }

    public double getCharsPerMinute() {
        return ((double) totalChars) / sessionTimeSeconds * 60d;
    }

    public int getSessionTimeSeconds() {
        return sessionTimeSeconds;
    }

    public void setSessionTimeSeconds(int sessionTimeSeconds) {
        this.sessionTimeSeconds = sessionTimeSeconds;
    }

    public int getTotalChars() {
        return totalChars;
    }

    public void setTotalChars(int totalChars) {
        this.totalChars = totalChars;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getMistakenWords() {
        return mistakenWords;
    }

    public void setMistakenWords(int mistakenWords) {
        this.mistakenWords = mistakenWords;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    @Override
    public String toString() {
        return "TypingSessionResult{" +
                "id="+ getId() +
                ", totalChars=" + totalChars +
                ", totalWords=" + totalWords +
                ", mistakenWords=" + mistakenWords +
                ", sessionDate=" + sessionDate +
                ", sessionTimeSeconds=" + sessionTimeSeconds +
                '}';
    }
}
