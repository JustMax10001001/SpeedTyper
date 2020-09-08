package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.resources.Resources;
import com.justsoft.speedtyper.ui.controls.Timer;
import com.justsoft.speedtyper.ui.controls.TypingControl;
import com.justsoft.speedtyper.ui.controls.Word;
import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.prefs.Preferences;

import static com.justsoft.speedtyper.ui.controllers.PreferencesController.*;


public class MainController {

    public GridPane root;
    public Hyperlink preferencesHyperlink;
    public Timer countdownTimer;
    public TypingControl typingControl;
    public Button restartButton;

    private Preferences preferences;

    @FXML
    private void initialize() {
        preferences = Preferences.userRoot().node("com/justsoft/speedtyper/preferences");

        setupTimer();
        updateTimer();
    }

    private void setupTimer() {
        countdownTimer.setOnFinished(() -> {
            //TODO: show results

            restartButton.setVisible(false);
            typingControl.reset();
        });
    }

    public void keyPressed(KeyEvent event) {
        if (!countdownTimer.isRunning()){
            countdownTimer.start();
            restartButton.setVisible(true);
        }

        if (!event.getText().trim().isEmpty()) {
            char ch = event.getText().charAt(0);
            typingControl.handleCharacter(event.isShiftDown() ? Character.toUpperCase(ch) : ch);
        }
        if (event.getCode() == KeyCode.SPACE) {
            typingControl.handleSpace();
        }

        if (event.getCode() == KeyCode.BACK_SPACE) {
            typingControl.handleBackspace();
        }
        event.consume();
    }

    public void preferencesHyperlinkClick() {
        preferencesHyperlink.setVisited(false);

        try {
            Dialog<Void> preferencesDialog = new Dialog<>();
            preferencesDialog.setTitle("Preferences");
            DialogPane pane = new DialogPane();
            pane.setContent(Resources.loadForm("preferences_form"));
            pane.getButtonTypes().add(ButtonType.CLOSE);
            preferencesDialog.setDialogPane(pane);

            preferencesDialog.showAndWait();

            updateTimer();
        } catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load preferences form");
        }
    }

    private void updateTimer() {
        countdownTimer.setTimerLength(preferences.getInt(TIMER_LENGTH_KEY, 60));
    }

    public void restartButtonClick() {
        restartButton.setVisible(false);
        countdownTimer.cancel();
        typingControl.reset();
    }
}
