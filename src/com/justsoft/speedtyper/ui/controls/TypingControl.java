package com.justsoft.speedtyper.ui.controls;

import com.justsoft.speedtyper.Dictionary;
import com.justsoft.speedtyper.resources.Resources;
import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.LinkedList;

public class TypingControl extends HBox {

    private static final int BUFFERED_WORDS = 12;

    @FXML
    private InputFlowingTextBox inputTextBox;
    @FXML
    private OutputFlowingTextBox outputTextBox;

    private Dictionary dictionary;

    private final LinkedList<String> wordBuffer = new LinkedList<>();
    private String inputBuffer, activeWord;

    public TypingControl() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/justsoft/speedtyper/resources/controls/typing_control.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load typing_control fxml");
        }
    }

    @FXML
    private void initialize() {
        this.getStyleClass().add("typing-control");
        populateDictionary();
    }

    private boolean isInputCorrect() {
        return activeWord.indexOf(inputBuffer) == 0;
    }

    private void updateTextBoxes() {
        if (isInputCorrect()) {
            inputTextBox.updateActiveWord(inputBuffer, true);

            outputTextBox.updateActiveWord(inputBuffer.isEmpty() ? activeWord : activeWord.substring(inputBuffer.length()));
        } else {
            inputTextBox.updateActiveWord(inputBuffer, false);
        }
    }

    private void flushAndPrepareTextBoxes() {
        inputTextBox.flushActiveWord();
        outputTextBox.flushActiveWord();

        inputTextBox.startNextWord();
        outputTextBox.startNextWord();
    }

    private void prepareForNextWord() {
        inputBuffer = "";
        activeWord = wordBuffer.pollFirst();

        appendRandomWordToBuffer();
    }

    // --- key handlers ---

    public void handleBackspace() {
        if (inputBuffer.length() == 0)
            return;

        inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);
        updateTextBoxes();
    }

    public void handleCharacter(char inputChar) {
        inputBuffer += inputChar;

        updateTextBoxes();
    }

    public void handleSpace() {
        if (inputBuffer.isEmpty())
            return;

        inputTextBox.updateActiveWord(inputBuffer, inputBuffer.equals(activeWord));

        flushAndPrepareTextBoxes();
        prepareForNextWord();
    }

    // --- handlers ---

    public void reset() {
        inputTextBox.reset();
        outputTextBox.reset();

        wordBuffer.clear();

        postInitialize();
    }

    private void appendRandomWordToBuffer() {
        String word = dictionary.getRandomDictionaryWord();
        outputTextBox.addBufferedWord(word);
        wordBuffer.add(word);
    }

    private void populateDictionary() {
        Task<Dictionary> dictionaryPopulateTask = createPopulateDictionaryTask();
        dictionaryPopulateTask.setOnSucceeded(event -> {
            this.dictionary = dictionaryPopulateTask.getValue();
            Platform.runLater(this::postInitialize);
        });
        dictionaryPopulateTask.setOnFailed(event -> {
            dictionaryPopulateTask.getException().printStackTrace();

            ExceptionDialog.show(dictionaryPopulateTask.getException(), "Could not load dictionary");
        });
        final Thread populateThread = new Thread(dictionaryPopulateTask);
        populateThread.setDaemon(false);
        populateThread.start();
    }

    private void fillWordBuffer() {
        for (int i = 0; i < BUFFERED_WORDS; i++) {
            appendRandomWordToBuffer();
        }
    }

    private void postInitialize() {
        fillWordBuffer();
        prepareForNextWord();

        inputTextBox.startNextWord();
        outputTextBox.startNextWord();
    }

    private Task<Dictionary> createPopulateDictionaryTask() {
        return new Task<Dictionary>() {
            @Override
            protected Dictionary call() throws Exception {
                Dictionary dictionary = new Dictionary(Resources.loadFile("dictionary.txt"));
                dictionary.load();
                return dictionary;
            }
        };
    }
}
