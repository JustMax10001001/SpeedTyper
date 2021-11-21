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

import static com.justsoft.speedtyper.util.Objects.notNull;

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
        statValueLabel.textProperty()
                      .bind(Bindings.createStringBinding(
                              () -> notNull(statValueProperty.get(), "null").toString(),
                              statValueProperty)
                      );

        subjectiveStatQualityProperty.addListener((observableValue, beforeValue, afterValue) -> {
            var oldClass = switch (beforeValue) {
                case BAD -> IS_BAD_RESULT;
                case NORMAL -> IS_NORMAL_RESULT;
                case GOOD -> IS_GOOD_RESULT;
                case NONE -> null;
            };

            var newClass = switch (afterValue) {
                case BAD -> IS_BAD_RESULT;
                case NORMAL -> IS_NORMAL_RESULT;
                case GOOD -> IS_GOOD_RESULT;
                case NONE -> null;
            };

            if (oldClass != null) {
                statValueLabel.pseudoClassStateChanged(oldClass, false);
            }

            if (newClass != null) {
                statValueLabel.pseudoClassStateChanged(newClass, true);
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
