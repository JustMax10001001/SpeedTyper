package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.SubjectiveStatQuality;
import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.ui.controls.StatControl;
import com.justsoft.speedtyper.util.Bundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;

public class TypingResultDisplayForm extends ControllerWithParameters {

    private final ViewModel viewModel = new ViewModel();
    private final SessionResultsRepository resultsRepository = SessionResultsRepository.getPreferredInstance();

    @FXML private StatControl<Double> wordsPerMinuteStat;
    @FXML private StatControl<Double> charsPerMinuteStat;
    @FXML private StatControl<Integer> mistakesCountStat;

    @Override
    void initialize(Bundle parameters) {
        final int resultId = parameters.getInt("result_id");
        final TypingSessionResult result = resultsRepository.getById(resultId);
        if (result == null)
            throw new IllegalArgumentException("There are no results with id = " + resultId + " in repository");

        viewModel.resultProperty.set(result);

        wordsPerMinuteStat.statNamePropertyProperty().set("WPM");
        wordsPerMinuteStat.statValuePropertyProperty().bind(viewModel.wordsPerMinute);
        wordsPerMinuteStat.subjectiveStatQualityPropertyProperty().bind(viewModel.charsPerMinuteSubjective);

        charsPerMinuteStat.statNamePropertyProperty().set("CPM");
        charsPerMinuteStat.statValuePropertyProperty().bind(viewModel.charsPerMinute);
        charsPerMinuteStat.subjectiveStatQualityPropertyProperty().bind(viewModel.charsPerMinuteSubjective);

        mistakesCountStat.statNamePropertyProperty().set("Mistakes");
        mistakesCountStat.statValuePropertyProperty().bind(viewModel.mistakes);
        mistakesCountStat.subjectiveStatQualityPropertyProperty().bind(viewModel.mistakesSubjective);
    }

    private static class ViewModel {

        private final SimpleObjectProperty<TypingSessionResult> resultProperty = new SimpleObjectProperty<>();

        private final ObjectBinding<Double> wordsPerMinute = Bindings.createObjectBinding(() -> {
            if (resultProperty.get() != null)
                return round(resultProperty.get().getWordsPerMinute(), 1);
            return 0d;
        }, resultProperty);

        private final ObjectBinding<Double> charsPerMinute = Bindings.createObjectBinding(() -> {
            if (resultProperty.get() != null)
                return round(resultProperty.get().getCharsPerMinute(), 1);
            return 0d;
        }, resultProperty);

        private final ObjectBinding<SubjectiveStatQuality> charsPerMinuteSubjective = Bindings.createObjectBinding(() -> {
            if (charsPerMinute.get() != null) {
                if (charsPerMinute.get() < 140)
                    return SubjectiveStatQuality.BAD;
                else if (charsPerMinute.get() < 330)
                    return SubjectiveStatQuality.NORMAL;
                else
                    return SubjectiveStatQuality.GOOD;
            }
            return SubjectiveStatQuality.NONE;
        }, charsPerMinute);

        private final ObjectBinding<Integer> mistakes = Bindings.createObjectBinding(() -> {
            if (resultProperty.get() != null)
                return resultProperty.get().getMistakenWords();
            return 0;
        }, resultProperty);

        private final ObjectBinding<SubjectiveStatQuality> mistakesSubjective = Bindings.createObjectBinding(() -> {
            if (mistakes.get() != null) {
                if (mistakes.get() < 1)
                    return SubjectiveStatQuality.GOOD;
                else if (mistakes.get() < 3)
                    return SubjectiveStatQuality.NORMAL;
                else
                    return SubjectiveStatQuality.BAD;
            }
            return SubjectiveStatQuality.NONE;
        }, mistakes);

        private double round(double value, int decimalPlaces) {
            double factor = Math.pow(10, decimalPlaces);
            return ((int) (value * factor)) / factor;
        }
    }
}
