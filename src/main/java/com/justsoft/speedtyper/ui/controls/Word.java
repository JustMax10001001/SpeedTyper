package com.justsoft.speedtyper.ui.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.text.Text;

public class Word extends Text {
    private static final PseudoClass IS_EDITED_PSEUDO_CLASS = PseudoClass.getPseudoClass("is-edited");
    private static final PseudoClass INPUT_PSEUDO_CLASS = PseudoClass.getPseudoClass("input");
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

    private final BooleanProperty isEdited = new SimpleBooleanProperty(false);
    private final BooleanProperty isInput = new SimpleBooleanProperty(false);
    private final BooleanProperty isError = new SimpleBooleanProperty(false);

    public Word() {
        super();
        initialize();
    }

    public Word(String val) {
        super(val);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add("flowing-text");
        initListeners();
    }

    private void initListeners() {
        isEdited.addListener(e -> pseudoClassStateChanged(IS_EDITED_PSEUDO_CLASS, isEdited.get()));
        isInput.addListener(e -> pseudoClassStateChanged(INPUT_PSEUDO_CLASS, isInput.get()));
        isError.addListener(e -> pseudoClassStateChanged(ERROR_PSEUDO_CLASS, isError.get()));
    }

    public boolean isEdited() {
        return isEdited.get();
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited.set(isEdited);
    }

    public boolean isInput() {
        return isInput.get();
    }

    public void setIsInput(boolean isInput) {
        this.isInput.set(isInput);
    }

    public boolean isError() {
        return isError.get();
    }

    public void setIsError(boolean isError) {
        this.isError.set(isError);
    }
}
