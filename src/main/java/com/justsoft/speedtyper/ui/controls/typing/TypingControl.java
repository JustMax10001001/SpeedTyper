package com.justsoft.speedtyper.ui.controls.typing;

import com.justsoft.speedtyper.Dictionary;
import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.ui.dialogs.ExceptionAlert;
import com.justsoft.speedtyper.util.Resources;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;

import static com.justsoft.speedtyper.util.Loops.repeat;

public class TypingControl extends HBox {

    private static final int BUFFERED_WORDS = 32;

    @FXML
    private InputFlowingTextBox inputTextBox;
    @FXML
    private OutputFlowingTextBox outputTextBox;

    private Dictionary dictionary;

    private final LinkedList<String> wordBuffer = new LinkedList<>();
    private String inputBuffer, activeWord;

    private final ReadOnlyStringWrapper currentWord = new ReadOnlyStringWrapper("");
    private final ReadOnlyIntegerWrapper correctWordCount = new ReadOnlyIntegerWrapper(0);
    private final ReadOnlyIntegerWrapper characterCount = new ReadOnlyIntegerWrapper(0);
    private final ReadOnlyIntegerWrapper mistakesCount = new ReadOnlyIntegerWrapper(0);

    public TypingControl() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls/typing_control.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            ExceptionAlert.show(e, "Unable to load typing_control fxml");
        }
    }

    @FXML
    private void initialize() {
        this.getStyleClass().add("typing-control");
        populateDictionary();
    }

    private boolean isInputPartiallyCorrect() {
        return this.activeWord.indexOf(this.inputBuffer) == 0;
    }

    private void updateTextBoxes() {
        if (isInputPartiallyCorrect()) {
            this.inputTextBox.updateActiveWord(this.inputBuffer, true);

            this.outputTextBox.updateActiveWord(this.inputBuffer.isEmpty() ? this.activeWord : this.activeWord.substring(this.inputBuffer.length()));
        } else {
            this.inputTextBox.updateActiveWord(this.inputBuffer, false);
        }
    }

    private void flushAndPrepareTextBoxes() {
        if (isInputPartiallyCorrect() && this.activeWord.equals(this.inputBuffer)) {
            this.correctWordCount.set(this.correctWordCount.get() + 1);
            this.characterCount.set(this.activeWord.length() + this.characterCount.get());
        } else {
            this.mistakesCount.set(this.mistakesCount.get() + 1);
        }

        this.inputTextBox.flushActiveWord();
        this.outputTextBox.flushActiveWord();

        this.inputTextBox.startNextWord();
        this.outputTextBox.startNextWord();
    }

    private void prepareForNextWord() {
        this.inputBuffer = "";
        this.activeWord = this.wordBuffer.pollFirst();
        this.currentWord.set(this.activeWord);

        appendRandomWordToBuffer();
    }

    // --- key handlers ---

    public void handleBackspace() {
        if (this.inputBuffer.length() == 0)
            return;

        this.inputBuffer = this.inputBuffer.substring(0, this.inputBuffer.length() - 1);
        updateTextBoxes();
    }

    public void handleCharacter(char inputChar) {
        this.inputBuffer += inputChar;

        updateTextBoxes();
    }

    public void handleSpace() {
        if (this.inputBuffer.isEmpty())
            return;

        this.inputTextBox.updateActiveWord(this.inputBuffer, this.inputBuffer.equals(this.activeWord));

        flushAndPrepareTextBoxes();
        prepareForNextWord();
    }

    // --- handlers ---

    public void reset() {
        this.inputTextBox.reset();
        this.outputTextBox.reset();

        this.wordBuffer.clear();

        this.characterCount.set(0);
        this.correctWordCount.set(0);
        this.mistakesCount.set(0);
        this.currentWord.set("");

        postInitialize();
    }

    public TypingResult getSessionResult(int sessionTime) {
        return new TypingResult(
                this.characterCount.get(),
                this.correctWordCount.get(),
                this.mistakesCount.get(),
                LocalDate.now(),
                sessionTime);
    }

    private void appendRandomWordToBuffer() {
        String word = this.dictionary.getRandomDictionaryWord();
        this.outputTextBox.addBufferedWord(word);
        this.wordBuffer.add(word);
    }

    private void populateDictionary() {
        Task<Dictionary> dictionaryPopulateTask = createPopulateDictionaryTask();
        dictionaryPopulateTask.setOnSucceeded(event -> {
            this.dictionary = dictionaryPopulateTask.getValue();
            Platform.runLater(this::postInitialize);
        });
        dictionaryPopulateTask.setOnFailed(event -> {
            dictionaryPopulateTask.getException().printStackTrace();

            ExceptionAlert.show(dictionaryPopulateTask.getException(), "Could not load dictionary");
        });
        Thread populateThread = new Thread(dictionaryPopulateTask);
        populateThread.setDaemon(false);
        populateThread.start();
    }

    private void fillWordBuffer() {
        repeat(BUFFERED_WORDS, this::appendRandomWordToBuffer);
    }

    private void postInitialize() {
        fillWordBuffer();
        prepareForNextWord();

        this.inputTextBox.startNextWord();
        this.outputTextBox.startNextWord();
    }

    private Task<Dictionary> createPopulateDictionaryTask() {
        return new Task<>() {
            @Override
            protected Dictionary call() throws Exception {
                Dictionary dictionary = new Dictionary(Resources.getFileResourceAsStream("dictionary.txt"));
                dictionary.load();
                return dictionary;
            }
        };
    }
}
