package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.SubjectiveStatQuality;
import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.results.TypingResultsRepository;
import com.justsoft.speedtyper.ui.controls.StatControl;
import com.justsoft.speedtyper.util.Bundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;

public class TypingResultDisplayController extends ControllerWithParameters {

    private final ViewModel viewModel = new ViewModel();
    private final TypingResultsRepository resultsRepository = TypingResultsRepository.getInstance();

    @FXML private StatControl<Double> wordsPerMinuteStat;
    @FXML private StatControl<Double> charsPerMinuteStat;
    @FXML private StatControl<Integer> mistakesCountStat;

    @Override
    void initialize(Bundle parameters) {
        int resultId = parameters.getInt("result_id");
        TypingResult result = this.resultsRepository.getById(resultId);
        if (result == null)
            throw new IllegalArgumentException("There are no results with id = " + resultId + " in repository");

        this.viewModel.resultProperty.set(result);

        this.wordsPerMinuteStat.statNamePropertyProperty().set("WPM");
        this.wordsPerMinuteStat.statValuePropertyProperty().bind(this.viewModel.wordsPerMinute);
        this.wordsPerMinuteStat.subjectiveStatQualityPropertyProperty().bind(this.viewModel.charsPerMinuteSubjective);

        this.charsPerMinuteStat.statNamePropertyProperty().set("CPM");
        this.charsPerMinuteStat.statValuePropertyProperty().bind(this.viewModel.charsPerMinute);
        this.charsPerMinuteStat.subjectiveStatQualityPropertyProperty().bind(this.viewModel.charsPerMinuteSubjective);

        this.mistakesCountStat.statNamePropertyProperty().set("Mistakes");
        this.mistakesCountStat.statValuePropertyProperty().bind(this.viewModel.mistakes);
        this.mistakesCountStat.subjectiveStatQualityPropertyProperty().bind(this.viewModel.mistakesSubjective);
    }

    private static class ViewModel {

        private final SimpleObjectProperty<TypingResult> resultProperty = new SimpleObjectProperty<>();

        private final ObjectBinding<Double> wordsPerMinute = Bindings.createObjectBinding(() -> {
            if (this.resultProperty.get() != null)
                return round(this.resultProperty.get().getWordsPerMinute(), 1);
            return 0d;
        }, this.resultProperty);

        private final ObjectBinding<Double> charsPerMinute = Bindings.createObjectBinding(() -> {
            if (this.resultProperty.get() != null)
                return round(this.resultProperty.get().getCharsPerMinute(), 1);
            return 0d;
        }, this.resultProperty);

        private final ObjectBinding<SubjectiveStatQuality> charsPerMinuteSubjective = Bindings.createObjectBinding(() -> {
            if (this.charsPerMinute.get() != null) {
                if (this.charsPerMinute.get() < 140)
                    return SubjectiveStatQuality.BAD;
                else if (this.charsPerMinute.get() < 330)
                    return SubjectiveStatQuality.NORMAL;
                else
                    return SubjectiveStatQuality.GOOD;
            }
            return SubjectiveStatQuality.NONE;
        }, this.charsPerMinute);

        private final ObjectBinding<Integer> mistakes = Bindings.createObjectBinding(() -> {
            if (this.resultProperty.get() != null)
                return this.resultProperty.get().mistakenWords();
            return 0;
        }, this.resultProperty);

        private final ObjectBinding<SubjectiveStatQuality> mistakesSubjective = Bindings.createObjectBinding(() -> {
            if (this.mistakes.get() != null) {
                if (this.mistakes.get() < 1)
                    return SubjectiveStatQuality.GOOD;
                else if (this.mistakes.get() < 3)
                    return SubjectiveStatQuality.NORMAL;
                else
                    return SubjectiveStatQuality.BAD;
            }
            return SubjectiveStatQuality.NONE;
        }, this.mistakes);

        private double round(double value, int decimalPlaces) {
            double factor = Math.pow(10, decimalPlaces);
            return ((int) (value * factor)) / factor;
        }
    }
}
