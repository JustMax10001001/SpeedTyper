package com.justsoft.speedtyper.ui.controls.typing;

import com.justsoft.speedtyper.ui.controls.Word;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;

public class InputFlowingTextBox extends AbstractFlowingTextBox {
    private static final double PREFERRED_WIDTH = 32 * 1024;

    @FXML
    private TextFlow inputTextFlowBox;
    @FXML
    private Region filler;

    public InputFlowingTextBox() {
        super("flowing_input_control");
    }

    @FXML
    @Override
    void initialize() {
        filler.setPrefWidth(PREFERRED_WIDTH);
        this.hvalueProperty().bind(inputTextFlowBox.widthProperty());
    }

    @Override
    public void flushActiveWord() {
        activeWord.setIsEdited(false);
        activeWord = null;
        inputTextFlowBox.getChildren().add(new Word(" "));
    }

    public void startNextWord() {
        activeWord = new Word();
        activeWord.setIsInput(true);
        inputTextFlowBox.getChildren().add(activeWord);
        activeWord.setIsEdited(true);
    }

    @Override
    void reset() {
        inputTextFlowBox.getChildren().clear();
        activeWord = null;
    }
}
