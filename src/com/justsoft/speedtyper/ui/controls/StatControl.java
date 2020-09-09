package com.justsoft.speedtyper.ui.controls;

import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import com.justsoft.speedtyper.util.Resources;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class StatControl<T> extends VBox {

    @FXML
    private Label statNameLabel;
    @FXML
    private Label statValueLabel;

    private final ObjectProperty<T> statValueProperty = new SimpleObjectProperty<>();
    private final StringProperty statNameProperty = new SimpleStringProperty("name");

    public StatControl() {
        FXMLLoader loader = Resources.createLoaderForControl("stat_control");
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load stat fxml");
        }
    }

    @FXML
    private void initialize() {
        statNameLabel.textProperty().bind(statNameProperty);
        statValueLabel.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    if (statValueProperty.get() == null)
                        return "null";
                    else
                        return statValueProperty.get().toString();
                }, statValueProperty)
        );
    }

    public StringProperty statNamePropertyProperty() {
        return statNameProperty;
    }

    public ObjectProperty<T> statValuePropertyProperty() {
        return statValueProperty;
    }
}
