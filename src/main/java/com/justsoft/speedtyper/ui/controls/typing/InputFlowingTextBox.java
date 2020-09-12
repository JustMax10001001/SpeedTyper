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
        filler.setPrefWidth(Screen.getPrimary().getBounds().getWidth() / 2);
        this.hvalueProperty().bind(inputTextFlowBox.widthProperty());
    }

    @Override
    public void flushActiveWord() {
        activeWord.setIsEdited(false);
        activeWord = null;
        inputTextFlowBox.getChildren().add(new Word(" "));
        cleanUpPane();
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

    private void cleanUpPane() {
        Bounds paneBounds = this.localToScene(this.getBoundsInLocal());
        for (Node n : FXCollections.unmodifiableObservableList(inputTextFlowBox.getChildren())) {
            Bounds nodeBounds = n.localToScene(n.getBoundsInLocal());
            if (!paneBounds.intersects(nodeBounds)) {
                if (n instanceof Text && !((Text) n).textProperty().isNotEmpty().get())
                    continue;
                inputTextFlowBox.getChildren().remove(n);
            }
        }
    }
}
