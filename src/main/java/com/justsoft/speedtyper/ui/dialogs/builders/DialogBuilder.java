package com.justsoft.speedtyper.ui.dialogs.builders;

import com.justsoft.speedtyper.util.Bundle;
import com.justsoft.speedtyper.util.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogBuilder<TResult> {
    private final List<ButtonType> buttonTypes = new ArrayList<>();

    private String title = "Dialog";
    private String formResource;
    private Bundle params = null;

    public DialogBuilder<TResult> withTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogBuilder<TResult> withFormResource(String resource) {
        this.formResource = resource;

        return this;
    }

    public DialogBuilder<TResult> withParams(Bundle params) {
        this.params = params;

        return this;
    }

    public DialogBuilder<TResult> withButtons(ButtonType... buttonTypes) {
        this.buttonTypes.clear();
        this.buttonTypes.addAll(Arrays.asList(buttonTypes));

        return this;
    }

    public Dialog<TResult> build() throws IOException {
        Dialog<TResult> dialog = new Dialog<>();
        dialog.setTitle(title);

        FXMLLoader loader;

        if (params == null) {
            loader = Resources.createLoaderForForm(formResource);
            loader.load();;
        } else {
            loader = Resources.createLoaderForForm(formResource, params);
        }

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(loader.getRoot());

        dialogPane.getButtonTypes().addAll(buttonTypes);

        return dialog;
    }
}
