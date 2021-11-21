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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultsDisplayController {

    private static final int MEDIAN_CHART_INDEX = 0;

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

        viewModel.statsList.addAll(processEntries(resultsRepository.getAll()));

        addChart("Median", viewModel.medianChartDataProperty);
        addChart("Average", viewModel.averageChartDataProperty);
        addChart("Max", viewModel.maxChartDataProperty);
    }

    private void addChart(String name, ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> dataProperty) {
        final var chartCount = cpmChart.getData().size();

        cpmChart.getData().add(new XYChart.Series<>(name, FXCollections.emptyObservableList()));
        cpmChart.getData().get(chartCount).dataProperty().bind(dataProperty);
    }

    private List<DailyStat> processEntries(List<TypingSessionResult> source) {
        return source.stream()
                     .collect(Collectors.groupingBy(TypingSessionResult::sessionDate))
                     .entrySet()
                     .stream()
                     .map(this::mapToDataPoint)
                     .collect(Collectors.toList());
    }

    private DailyStat mapToDataPoint(Map.Entry<LocalDate, List<TypingSessionResult>> entry) {
        final var date = entry.getKey();
        final var dataList = entry.getValue();

        return new DailyStat(date, calculateMedian(dataList), calculateAverage(dataList), calculateMax(dataList));
    }

    private int calculateMedian(List<TypingSessionResult> resultList) {
        return (int) Stats.calculateMedian(resultList, TypingSessionResult::getCharsPerMinute);
    }

    private int calculateAverage(List<TypingSessionResult> resultList) {
        return (int) resultList.stream().mapToDouble(TypingSessionResult::getCharsPerMinute).average().orElse(0);
    }

    private int calculateMax(List<TypingSessionResult> resultList) {
        return (int) resultList.stream().mapToDouble(TypingSessionResult::getCharsPerMinute).max().orElse(0);
    }

    private static class ViewModel {
        private final ObjectProperty<LocalDate> dateSinceProperty = new SimpleObjectProperty<>(LocalDate.now().minusDays(7));
        private final ObjectProperty<LocalDate> dateUpToProperty = new SimpleObjectProperty<>(LocalDate.now());

        private final ObservableList<DailyStat> statsList = FXCollections.observableArrayList();

        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> medianChartDataProperty = createChartDataBinding(DailyStat::medianCpm);
        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> averageChartDataProperty = createChartDataBinding(DailyStat::averageCpm);
        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> maxChartDataProperty = createChartDataBinding(DailyStat::maxCpm);

        private boolean isBetweenIncludingEnd(LocalDate toCheck, LocalDate notBefore, LocalDate notAfter) {
            return toCheck.isAfter(notBefore) && (toCheck.isBefore(notAfter) || toCheck.isEqual(notAfter));
        }

        private ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> createChartDataBinding(Function<DailyStat, Integer> valueSelector) {
            return Bindings.createObjectBinding(() -> {
                final LocalDate dateSince = dateSinceProperty.get();
                final LocalDate dateUpTo = dateUpToProperty.get();

                return FXCollections.observableArrayList(
                        statsList.stream()
                                 .filter(data -> isBetweenIncludingEnd(data.resultDate(), dateSince, dateUpTo))
                                 .map(item -> new XYChart.Data<>(item.resultDate().toString(), valueSelector.apply(item)))
                                 .collect(Collectors.toList())
                );
            }, dateSinceProperty, dateUpToProperty, statsList);
        }
    }

    private static record DailyStat(LocalDate resultDate, int medianCpm, int averageCpm, int maxCpm) {}
}
