package com.justsoft.speedtyper.ui.controls;

import javafx.fxml.FXML;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;

public class OutputFlowingTextBox extends AbstractFlowingTextBox {

    @FXML
    private TextFlow outputTextFlowBox;

    private Word activeWord;

    private final LinkedList<Word> bufferedWords = new LinkedList<>();

    public OutputFlowingTextBox() {
        super("flowing_output_control");
    }

    @FXML
    void initialize(){

    }

    public void addBufferedWord(String word){
        bufferedWords.addLast(new Word(word + " "));
        outputTextFlowBox.getChildren().add(bufferedWords.getLast());
    }

    @Override
    public void updateActiveWord(String value) {
        this.activeWord.setText(value + " ");
    }

    @Override
    void flushActiveWord() {
        outputTextFlowBox.getChildren().remove(activeWord);
        bufferedWords.pollFirst();
        activeWord = null;
    }

    public void startNextWord(){
        activeWord = bufferedWords.getFirst();
    }

    @Override
    void reset() {
        outputTextFlowBox.getChildren().clear();
        bufferedWords.clear();
        activeWord = null;
    }
}
