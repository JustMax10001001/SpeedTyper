package com.justsoft.speedtyper.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

import java.util.prefs.Preferences;

public class PreferencesController {

    public static final String TIMER_LENGTH_KEY = "timerLength";

    public Spinner<Integer> minutesSpinner;
    public Spinner<Integer> secondsSpinner;

    private Preferences preferences;

    @FXML
    public void initialize() {
        preferences = Preferences.userRoot().node("com/justsoft/speedtyper/preferences");

        minutesSpinner.getValueFactory().setValue(preferences.getInt(TIMER_LENGTH_KEY, 60) / 60);
        secondsSpinner.getValueFactory().setValue(preferences.getInt(TIMER_LENGTH_KEY, 60) % 60);

        minutesSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) ->
                preferences.putInt(TIMER_LENGTH_KEY, newValue * 60 + secondsSpinner.getValue()));
        secondsSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) ->
                preferences.putInt(TIMER_LENGTH_KEY, minutesSpinner.getValue() * 60 + newValue));
    }
}
