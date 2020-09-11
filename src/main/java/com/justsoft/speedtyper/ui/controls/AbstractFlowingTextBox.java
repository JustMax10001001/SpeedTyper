package com.justsoft.speedtyper.ui.controls;

import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;

import java.io.IOException;

abstract class AbstractFlowingTextBox extends ScrollPane {

    protected Word activeWord;

    public AbstractFlowingTextBox(String controlName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls/" + controlName + ".fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            this.getStyleClass().setAll("flowing-text-box");
        } catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load " + controlName + " fxml");
        }
    }

    @FXML
    abstract void initialize();

    public void updateActiveWord(String value){
        activeWord.textProperty().set(value);
    }

    public void updateActiveWord(String value, boolean isCorrect) {
        updateActiveWord(value);
        activeWord.setIsError(!isCorrect);
    }

    abstract void flushActiveWord();

    abstract void startNextWord();

    abstract void reset();
}
