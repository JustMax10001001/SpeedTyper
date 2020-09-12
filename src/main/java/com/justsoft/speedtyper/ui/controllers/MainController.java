package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.util.Bundle;
import com.justsoft.speedtyper.util.Resources;
import com.justsoft.speedtyper.ui.controls.Timer;
import com.justsoft.speedtyper.ui.controls.typing.TypingControl;
import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.prefs.Preferences;

import static com.justsoft.speedtyper.ui.controllers.PreferencesController.*;


public class MainController {

    @FXML private GridPane root;
    @FXML private Hyperlink preferencesHyperlink;
    @FXML private Timer countdownTimer;
    @FXML private TypingControl typingControl;
    @FXML private Button restartButton;
    @FXML private Hyperlink resultsHyperlink;

    private Preferences preferences;
    private final SessionResultsRepository resultsService = SessionResultsRepository.getPreferredInstance();

    @FXML
    private void initialize() {
        preferences = Preferences.userRoot().node("com/justsoft/speedtyper/preferences");

        setupTimer();
        updateTimer();
    }

    private void setupTimer() {
        countdownTimer.setOnFinished(() -> {
            TypingSessionResult result = typingControl.getSessionResult(preferences.getInt(TIMER_LENGTH_KEY, 60));
            result.setSessionDate(LocalDate.now());
            resultsService.save(result);
            System.out.println(result.toString());

            showResult(result);

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

    private void showResult(TypingSessionResult result) {
        try {
            Dialog<Void> resultDialog = new Dialog<>();
            resultDialog.setTitle("Result");
            Bundle params = new Bundle();
            params.set("result_id", result.getId());
            FXMLLoader loader = Resources.createLoaderForFormWithParameters("typing_result_display_form", params);
            DialogPane dialogPane = resultDialog.getDialogPane();
            dialogPane.setContent(loader.getRoot());
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);
            Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
            closeButton.setDefaultButton(false);
            dialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {   // prevent space from closing the result screen
                if (keyEvent.getCode() == KeyCode.SPACE)
                    keyEvent.consume();
            });


            resultDialog.showAndWait();
        }catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load result form");
        }
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

    public void resultsHyperlinkClick() {
        resultsHyperlink.setVisited(false);

        try {
            Dialog<Void> resultsDialog = new Dialog<>();
            resultsDialog.setTitle("Results");
            resultsDialog.getDialogPane().setContent(Resources.loadForm("results_display_form"));
            resultsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            resultsDialog.showAndWait();
        } catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load results form");
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
