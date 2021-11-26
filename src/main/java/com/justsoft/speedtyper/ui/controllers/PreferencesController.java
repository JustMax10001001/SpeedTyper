package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.services.prefs.PreferenceService;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

public class PreferencesController {
    private static final int MIN_SESSION_DURATION_SECONDS = 10;

    private final PreferenceService preferences;

    @FXML
    private Spinner<Integer> minutesSpinner;
    @FXML
    private Spinner<Integer> secondsSpinner;


    public PreferencesController() {
        this.preferences = PreferenceService.getInstance();
    }

    @FXML
    public void initialize() {
        initSessionTimeSpinners();
    }

    private void initSessionTimeSpinners() {
        var sessionTimeSeconds = this.preferences.sessionTime();

        initSpinnerValues(sessionTimeSeconds);

        initSpinnerListeners();
    }

    private void updateSessionTime(int minutes, int seconds) {
        var secondsValue = minutes * 60 + seconds;

        if (secondsValue < MIN_SESSION_DURATION_SECONDS) {
            initSpinnerValues(MIN_SESSION_DURATION_SECONDS);

            return;
        }

        this.preferences.setSessionTime(secondsValue);
    }

    private void updateMinutes(int newMinutes) {
        updateSessionTime(newMinutes, this.secondsSpinner.getValue());
    }

    private void updateSeconds(int newSeconds) {
        updateSessionTime(this.minutesSpinner.getValue(), newSeconds);
    }

    private void initSpinnerListeners() {
        this.minutesSpinner.getValueFactory()
                           .valueProperty()
                           .addListener((v, o, newMinutes) -> updateMinutes(newMinutes));

        this.secondsSpinner.getValueFactory()
                           .valueProperty()
                           .addListener((v, o, newSeconds) -> updateSeconds(newSeconds));
    }

    private void initSpinnerValues(int sessionTimeSeconds) {

        this.minutesSpinner.getValueFactory().setValue(sessionTimeSeconds / 60);
        this.secondsSpinner.getValueFactory().setValue(sessionTimeSeconds % 60);
    }
}
