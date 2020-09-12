package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.util.Stats;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultsDisplayController {

    @FXML
    private DatePicker dateSincePicker;
    @FXML
    private DatePicker dateUpToPicker;
    @FXML
    private LineChart<String, Integer> cpmChart;

    private final ViewModel viewModel = new ViewModel();
    private final SessionResultsRepository resultsRepository = SessionResultsRepository.getPreferredInstance();

    @FXML
    public void initialize() {
        dateSincePicker.valueProperty().bindBidirectional(viewModel.dateSinceProperty);
        dateUpToPicker.valueProperty().bindBidirectional(viewModel.dateUpToProperty);

        viewModel.resultSourceList.addAll(processEntries(resultsRepository.getAll()));
        cpmChart.getData().add(new XYChart.Series<>("Characters per minute", FXCollections.emptyObservableList()));
        cpmChart.getData().get(0).dataProperty().bind(viewModel.filteredResultsCpm);
    }

    private List<XYChart.Data<LocalDate, Integer>> processEntries(List<TypingSessionResult> source) {
        Map<LocalDate, List<TypingSessionResult>> map = new HashMap<>();
        source.forEach(item -> {
            if (!map.containsKey(item.getSessionDate())) {
                map.put(item.getSessionDate(), new ArrayList<>());
            }
            map.get(item.getSessionDate()).add(item);
        });
        List<XYChart.Data<LocalDate, Integer>> outputList = new ArrayList<>();
        map.forEach((date, entryList) -> outputList.add(new XYChart.Data<>(date, (int) Stats.calculateMedian(entryList, TypingSessionResult::getCharsPerMinute))));
        return outputList;
    }

    private static class ViewModel {

        private final ObjectProperty<LocalDate> dateSinceProperty = new SimpleObjectProperty<>(LocalDate.of(1970, 1, 1));
        private final ObjectProperty<LocalDate> dateUpToProperty = new SimpleObjectProperty<>(LocalDate.now());

        private final ObservableList<XYChart.Data<LocalDate, Integer>> resultSourceList = FXCollections.observableArrayList();

        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> filteredResultsCpm = Bindings.createObjectBinding(() -> {
            ObservableList<XYChart.Data<String, Integer>> list = FXCollections.observableArrayList();
            final LocalDate dateSince = dateSinceProperty.get();
            final LocalDate dateUpTo = dateUpToProperty.get();
            list.addAll(
                    resultSourceList
                            .stream()
                            .filter(item ->
                                    item.getXValue().isAfter(dateSince) && (item.getXValue().isBefore(dateUpTo) || item.getXValue().isEqual(dateUpTo))
                            )
                            .map(item -> new XYChart.Data<>(item.getXValue().toString(), item.getYValue()))
                            .collect(Collectors.toList())

            );
            return list;
        }, dateSinceProperty, dateUpToProperty, resultSourceList);
    }
}
