package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.Bundle;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ControllerWithParameters implements Initializable {

    private Bundle parameters;

    public void initialize() { }

    public void initialize(URL url, ResourceBundle bundle) { }

    abstract void initialize(Bundle parameters);

    public void setParametersAndInitialize(Bundle parameters) {
        this.parameters = parameters;
        initialize(parameters);
    }

    protected Bundle getParameters() {
        return parameters;
    }
}
