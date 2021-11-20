package com.justsoft.speedtyper.ui.controls;

import com.justsoft.speedtyper.model.SubjectiveStatQuality;
import com.justsoft.speedtyper.ui.dialogs.ExceptionAlert;
import com.justsoft.speedtyper.util.Resources;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class StatControl<T> extends VBox {

    private static final PseudoClass IS_GOOD_RESULT = PseudoClass.getPseudoClass("good");
    private static final PseudoClass IS_NORMAL_RESULT = PseudoClass.getPseudoClass("normal");
    private static final PseudoClass IS_BAD_RESULT = PseudoClass.getPseudoClass("bad");

    @FXML
    private Label statNameLabel;
    @FXML
    private Label statValueLabel;

    private final ObjectProperty<T> statValueProperty = new SimpleObjectProperty<>();
    private final StringProperty statNameProperty = new SimpleStringProperty("name");
    private final ObjectProperty<SubjectiveStatQuality> subjectiveStatQualityProperty
            = new SimpleObjectProperty<>(SubjectiveStatQuality.NONE);

    public StatControl() {
        FXMLLoader loader = Resources.createLoaderForControl("stat_control");
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            ExceptionAlert.show(e, "Unable to load stat fxml");
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

        subjectiveStatQualityProperty.addListener((observableValue, beforeValue, afterValue) -> {
            switch (beforeValue){
                case BAD:
                    statValueLabel.pseudoClassStateChanged(IS_BAD_RESULT, false);
                    break;
                case NORMAL:
                    statValueLabel.pseudoClassStateChanged(IS_NORMAL_RESULT, false);
                    break;
                case GOOD:
                    statValueLabel.pseudoClassStateChanged(IS_GOOD_RESULT, false);
                    break;
            }
            switch (afterValue) {
                case BAD:
                    statValueLabel.pseudoClassStateChanged(IS_BAD_RESULT, true);
                    break;
                case NORMAL:
                    statValueLabel.pseudoClassStateChanged(IS_NORMAL_RESULT, true);
                    break;
                case GOOD:
                    statValueLabel.pseudoClassStateChanged(IS_GOOD_RESULT, true);
                    break;
            }
        });
    }

    public StringProperty statNamePropertyProperty() {
        return statNameProperty;
    }

    public ObjectProperty<T> statValuePropertyProperty() {
        return statValueProperty;
    }

    public ObjectProperty<SubjectiveStatQuality> subjectiveStatQualityPropertyProperty() {
        return subjectiveStatQualityProperty;
    }
}
