package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.ui.dialogs.builders.DialogBuilder;
import com.justsoft.speedtyper.util.Bundle;
import com.justsoft.speedtyper.ui.controls.Timer;
import com.justsoft.speedtyper.ui.controls.typing.TypingControl;
import com.justsoft.speedtyper.ui.dialogs.ExceptionAlert;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.prefs.Preferences;

import static com.justsoft.speedtyper.ui.controllers.PreferencesController.*;


public class MainController {
    private final int DEFAULT_TIMER_LENGTH = 60;

    @FXML
    private GridPane root;
    @FXML
    private Hyperlink preferencesHyperlink;
    @FXML
    private Timer countdownTimer;
    @FXML
    private TypingControl typingControl;
    @FXML
    private Button restartButton;
    @FXML
    private Hyperlink resultsHyperlink;

    private Preferences preferences;
    private final SessionResultsRepository resultsService = SessionResultsRepository.getPreferredInstance();

    @FXML
    private void initialize() {
        preferences = Preferences.userRoot().node("com/justsoft/speedtyper/preferences");

        setupTimer();
        updateTimer();
    }

    private void setupTimer() {
        countdownTimer.setOnFinished((interrupted) -> {
            if (!interrupted) {
                int sessionTime = preferences.getInt(TIMER_LENGTH_KEY, DEFAULT_TIMER_LENGTH);

                TypingSessionResult result = typingControl.getSessionResult(sessionTime);
                result.setSessionDate(LocalDate.now());

                resultsService.save(result);

                showResult(result);
            }

            restartButton.setVisible(false);
            typingControl.reset();
        });
    }

    public void keyPressed(KeyEvent event) {
        if (!countdownTimer.isRunning()) {
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
            Dialog<Void> resultDialog = new DialogBuilder<Void>()
                    .withTitle("Result")
                    .withFormResource("typing_result_display_form")
                    .withParams(createResultDialogParams(result))
                    .withButtons(ButtonType.CLOSE)
                    .build();

            disableSpaceInDialog(resultDialog);

            resultDialog.showAndWait();
        } catch (IOException e) {
            ExceptionAlert.show(e, "Unable to load result form");
        }
    }

    private void disableSpaceInDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();

        dialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {   // prevent space from closing the result screen
            if (keyEvent.getCode() == KeyCode.SPACE)
                keyEvent.consume();
        });
    }

    private Bundle createResultDialogParams(TypingSessionResult result) {
        Bundle params = new Bundle();
        params.set("result_id", result.getId());
        return params;
    }

    public void preferencesHyperlinkClick() {
        preferencesHyperlink.setVisited(false);

        try {
            new DialogBuilder<Void>()
                    .withTitle("Preferences")
                    .withFormResource("preferences_form")
                    .withButtons(ButtonType.CLOSE)
                    .build()
                    .showAndWait();

            updateTimer();
        } catch (IOException e) {
            ExceptionAlert.show(e, "Unable to load preferences form");
        }
    }

    public void resultsHyperlinkClick() {
        resultsHyperlink.setVisited(false);

        try {
            new DialogBuilder<Void>()
                    .withTitle("Results")
                    .withFormResource("results_display_form")
                    .withButtons(ButtonType.CLOSE)
                    .build()
                    .showAndWait();
        } catch (IOException e) {
            ExceptionAlert.show(e, "Unable to load results form");
        }
    }

    private void updateTimer() {
        countdownTimer.setTimerLength(preferences.getInt(TIMER_LENGTH_KEY, DEFAULT_TIMER_LENGTH));
    }

    public void restartButtonClick() {
        restartButton.setVisible(false);
        countdownTimer.cancel();
        typingControl.reset();
    }
}
