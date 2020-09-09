package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.util.Bundle;
import javafx.fxml.FXML;

public class TypingResultDisplayForm extends ControllerWithParameters{

    @Override
    void initialize(Bundle parameters) {
        Bundle params = parameters;
        System.out.println(params.getString("hello"));
    }
}
