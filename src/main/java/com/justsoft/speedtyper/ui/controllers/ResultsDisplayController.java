package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.repositories.results.TypingResultsRepository;
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
    private final TypingResultsRepository resultsRepository = TypingResultsRepository.getInstance();

    @FXML
    public void initialize() {
        this.dateSincePicker.valueProperty().bindBidirectional(this.viewModel.dateSinceProperty);
        this.dateUpToPicker.valueProperty().bindBidirectional(this.viewModel.dateUpToProperty);

        this.viewModel.statsList.addAll(processEntries(this.resultsRepository.getAll()));

        addChart("Median", this.viewModel.medianChartDataProperty);
        addChart("Average", this.viewModel.averageChartDataProperty);
        addChart("Max", this.viewModel.maxChartDataProperty);
    }

    private void addChart(String name, ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> dataProperty) {
        var chartCount = this.cpmChart.getData().size();

        this.cpmChart.getData().add(new XYChart.Series<>(name, FXCollections.emptyObservableList()));
        this.cpmChart.getData().get(chartCount).dataProperty().bind(dataProperty);
    }

    private List<DailyStat> processEntries(List<TypingResult> source) {
        return source.stream()
                     .collect(Collectors.groupingBy(TypingResult::sessionDate))
                     .entrySet()
                     .stream()
                     .sorted(Map.Entry.comparingByKey())
                     .map(this::mapToDataPoint)
                     .collect(Collectors.toList());
    }

    private DailyStat mapToDataPoint(Map.Entry<LocalDate, List<TypingResult>> entry) {
        var date = entry.getKey();
        var dataList = entry.getValue();

        return new DailyStat(date, calculateMedian(dataList), calculateAverage(dataList), calculateMax(dataList));
    }

    private int calculateMedian(List<TypingResult> resultList) {
        return (int) Stats.calculateMedian(resultList, TypingResult::getCharsPerMinute);
    }

    private int calculateAverage(List<TypingResult> resultList) {
        return (int) resultList.stream().mapToDouble(TypingResult::getCharsPerMinute).average().orElse(0);
    }

    private int calculateMax(List<TypingResult> resultList) {
        return (int) resultList.stream().mapToDouble(TypingResult::getCharsPerMinute).max().orElse(0);
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
                LocalDate dateSince = this.dateSinceProperty.get();
                LocalDate dateUpTo = this.dateUpToProperty.get();

                return FXCollections.observableArrayList(
                        this.statsList.stream()
                                      .filter(data -> isBetweenIncludingEnd(data.resultDate(), dateSince, dateUpTo))
                                      .map(item -> new XYChart.Data<>(item.resultDate().toString(), valueSelector.apply(item)))
                                      .collect(Collectors.toList())
                );
            }, this.dateSinceProperty, this.dateUpToProperty, this.statsList);
        }
    }

    private static record DailyStat(LocalDate resultDate, int medianCpm, int averageCpm, int maxCpm) {}
}
