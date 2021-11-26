package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.services.prefs.PreferenceService;
import com.justsoft.speedtyper.services.results.ResultService;
import com.justsoft.speedtyper.ui.controls.Timer;
import com.justsoft.speedtyper.ui.controls.typing.TypingController;
import com.justsoft.speedtyper.ui.dialogs.ExceptionAlert;
import com.justsoft.speedtyper.ui.dialogs.builders.DialogBuilder;
import com.justsoft.speedtyper.util.Bundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class MainController {

    private static final String PREFERENCES_TITLE = "Preferences";
    private static final String RESULTS_TITLE = "Results";

    private final ContextMenu mainContextMenu = new ContextMenu();

    private final PreferenceService preferences;
    private final ResultService resultsService;

    @FXML
    private GridPane root;
    @FXML
    private Hyperlink preferencesHyperlink;
    @FXML
    private Timer countdownTimer;
    @FXML
    private TypingController typingControl;
    @FXML
    private Button restartButton;
    @FXML
    private Hyperlink resultsHyperlink;

    public MainController() {
        this.preferences = PreferenceService.getInstance();
        this.resultsService = ResultService.getInstance();
    }

    @FXML
    private void initialize() {
        initContextMenu();

        setupTimer();
        updateTimer();
    }

    private void initContextMenu() {
        var preferencesItem = new MenuItem("_" + PREFERENCES_TITLE);
        preferencesItem.setMnemonicParsing(true);
        preferencesItem.setOnAction(e -> showPreferencesDialog());

        var resultItem = new MenuItem("_" + RESULTS_TITLE);
        resultItem.setMnemonicParsing(true);
        resultItem.setOnAction(e -> showResultsDialog());

        this.mainContextMenu.getItems().addAll(preferencesItem, resultItem);
        this.root.setOnContextMenuRequested(event -> this.mainContextMenu.show(this.root, event.getScreenX(), event.getScreenY()));
    }

    private void setupTimer() {
        this.countdownTimer.setOnFinished((interrupted) -> {
            if (!interrupted) {
                int sessionTime = this.preferences.sessionTime();

                var result = this.typingControl.getSessionResult(sessionTime);

                result = this.resultsService.saveResult(result);
                showResult(result);
            }

            this.restartButton.setVisible(false);
            this.typingControl.reset();
        });
    }

    public void keyPressed(KeyEvent event) {
        if ((event.getText().isBlank() || event.isShortcutDown() || event.isAltDown()) && event.getCode() != KeyCode.BACK_SPACE && event.getCode() != KeyCode.SPACE) {
            return;
        }

        if (!this.countdownTimer.isRunning()) {
            this.countdownTimer.start();
            this.restartButton.setVisible(true);
        }

        if (!event.getText().isBlank() && !event.isAltDown() && !event.isControlDown()) {
            char ch = event.getText().charAt(0);
            this.typingControl.handleCharacter(event.isShiftDown() ? Character.toUpperCase(ch) : ch);
        }

        if (event.getCode() == KeyCode.SPACE) {
            this.typingControl.handleSpace();
        }

        if (event.getCode() == KeyCode.BACK_SPACE) {
            this.typingControl.handleBackspace();
        }
        event.consume();
    }

    private void showResult(TypingResult result) {
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

    private Bundle createResultDialogParams(TypingResult result) {
        Bundle params = new Bundle();
        params.set("result_id", result.id());
        return params;
    }

    public void preferencesHyperlinkClick() {
        this.preferencesHyperlink.setVisited(false);

        showPreferencesDialog();
    }

    private void showPreferencesDialog() {
        try {
            new DialogBuilder<Void>().withTitle("Preferences")
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
        this.resultsHyperlink.setVisited(false);

        showResultsDialog();
    }

    private void showResultsDialog() {
        try {
            new DialogBuilder<Void>().withTitle("Results")
                                     .withFormResource("results_display_form")
                                     .withButtons(ButtonType.CLOSE)
                                     .build()
                                     .showAndWait();
        } catch (IOException e) {
            ExceptionAlert.show(e, "Unable to load results form");
        }
    }

    private void updateTimer() {
        this.countdownTimer.setTimerLength(this.preferences.sessionTime());
    }

    public void restartButtonClick() {
        this.restartButton.setVisible(false);
        this.countdownTimer.cancel();
        this.typingControl.reset();
    }
}
