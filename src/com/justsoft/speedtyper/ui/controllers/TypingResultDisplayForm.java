package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.services.SessionResultMapService;
import com.justsoft.speedtyper.util.Bundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

public class TypingResultDisplayForm extends ControllerWithParameters {



    private final ViewModel viewModel = new ViewModel();
    private final SessionResultsRepository resultsRepository = SessionResultsRepository.getPreferredInstance();

    @Override
    void initialize(Bundle parameters) {
        final int resultId = parameters.getInt("result_id");
        final TypingSessionResult result = resultsRepository.getById(resultId);
        if (result == null)
            throw new IllegalArgumentException("There are no results with id = " + resultId + " in repository");

        viewModel.resultProperty.set(result);
    }

    private static class ViewModel {

        private final SimpleObjectProperty<TypingSessionResult> resultProperty = new SimpleObjectProperty<>();

        private final DoubleBinding wordsPerMinute = Bindings.createDoubleBinding(() -> {
            if (resultProperty.get() != null)
                return round(resultProperty.get().getWordsPerMinute(), 1);
            return 0d;
        }, resultProperty);

        private final DoubleBinding charsPerMinute = Bindings.createDoubleBinding(() -> {
            if (resultProperty.get() != null)
                return round(resultProperty.get().getCharsPerMinute(), 1);
            return 0d;
        }, resultProperty);

        private final IntegerBinding mistakes = Bindings.createIntegerBinding(() -> {
            if (resultProperty.get() != null)
                return resultProperty.get().getMistakenWords();
            return 0;
        }, resultProperty);

        private double round(double value, int decimalPlaces) {
            double factor = Math.pow(10, decimalPlaces);
            return ((int) value * factor) / factor;
        }
    }
}
