package com.justsoft.speedtyper.ui.controllers;

import com.justsoft.speedtyper.model.entities.TypingResult;
import com.justsoft.speedtyper.services.prefs.PreferenceService;
import com.justsoft.speedtyper.services.results.ResultService;
import com.justsoft.speedtyper.util.Stats;
import javafx.beans.Observable;
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

    private final ViewModel viewModel = new ViewModel();
    private final ResultService resultRepository;
    private final PreferenceService preferences;

    @FXML
    private DatePicker dateSincePicker;
    @FXML
    private DatePicker dateUpToPicker;
    @FXML
    private LineChart<String, Integer> cpmChart;

    public ResultsDisplayController() {
        this.resultRepository = ResultService.getInstance();
        this.preferences = PreferenceService.getInstance();
    }

    @FXML
    public void initialize() {
        this.dateSincePicker.valueProperty().addListener(this::onDateNotBeforeChanged);
        this.dateUpToPicker.valueProperty().addListener(this::onDateNotAfterChanged);

        this.viewModel.setDateNotBeforeProperty(this.preferences.resultsNotBeforeTime());
        this.viewModel.setDateNotAfterProperty(this.preferences.resultsNotAfterTime());

        this.dateSincePicker.valueProperty().bindBidirectional(this.viewModel.dateNotBeforeProperty);
        this.dateUpToPicker.valueProperty().bindBidirectional(this.viewModel.dateNotAfterProperty);

        this.viewModel.statsList.addAll(processEntries(this.resultRepository.getAllResults()));

        addChart("Median", this.viewModel.medianChartDataProperty);
        addChart("Average", this.viewModel.averageChartDataProperty);
        addChart("Max", this.viewModel.maxChartDataProperty);
    }

    private void onDateNotBeforeChanged(Observable v, LocalDate oldDate, LocalDate newDate) {
        var dateNotAfter = this.viewModel.dateNotAfterProperty.get();

        if (dateNotAfter == null) {
            return;
        }

        if (newDate.isAfter(dateNotAfter)) {
            this.dateSincePicker.setValue(oldDate);
            return;
        }

        this.preferences.setResultsNotBeforeTime(newDate);
    }

    private void onDateNotAfterChanged(Observable v, LocalDate oldDate, LocalDate newDate) {
        var dateNotBefore = this.viewModel.dateNotBeforeProperty.get();

        if (dateNotBefore == null) {
            return;
        }

        if (newDate.isBefore(dateNotBefore)) {
            this.dateUpToPicker.setValue(oldDate);
            return;
        }

        this.preferences.setResultsNotAfterTime(newDate);
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
        return (int) Stats.calculateMedian(resultList, TypingResult::charsPerMinute);
    }

    private int calculateAverage(List<TypingResult> resultList) {
        return (int) resultList.stream().mapToDouble(TypingResult::charsPerMinute).average().orElse(0);
    }

    private int calculateMax(List<TypingResult> resultList) {
        return (int) resultList.stream().mapToDouble(TypingResult::charsPerMinute).max().orElse(0);
    }

    private static class ViewModel {
        private final ObjectProperty<LocalDate> dateNotBeforeProperty = new SimpleObjectProperty<>();
        private final ObjectProperty<LocalDate> dateNotAfterProperty = new SimpleObjectProperty<>();

        private final ObservableList<DailyStat> statsList = FXCollections.observableArrayList();

        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> medianChartDataProperty = createChartDataBinding(DailyStat::medianCpm);
        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> averageChartDataProperty = createChartDataBinding(DailyStat::averageCpm);
        private final ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> maxChartDataProperty = createChartDataBinding(DailyStat::maxCpm);

        public void setDateNotBeforeProperty(LocalDate dateNotBeforeProperty) {
            this.dateNotBeforeProperty.set(dateNotBeforeProperty);
        }

        public void setDateNotAfterProperty(LocalDate dateNotAfterProperty) {
            this.dateNotAfterProperty.set(dateNotAfterProperty);
        }

        private boolean isBetween(LocalDate toCheck, LocalDate notBefore, LocalDate notAfter) {
            return toCheck.isEqual(notBefore) || toCheck.isEqual(notAfter)
                    || toCheck.isAfter(notBefore) && toCheck.isBefore(notAfter);
        }

        private ObjectBinding<ObservableList<XYChart.Data<String, Integer>>> createChartDataBinding(Function<DailyStat, Integer> valueSelector) {
            return Bindings.createObjectBinding(() -> {
                LocalDate dateSince = this.dateNotBeforeProperty.get();
                LocalDate dateUpTo = this.dateNotAfterProperty.get();

                return FXCollections.observableArrayList(
                        this.statsList.stream()
                                      .filter(data -> isBetween(data.resultDate(), dateSince, dateUpTo))
                                      .map(item -> new XYChart.Data<>(item.resultDate().toString(), valueSelector.apply(item)))
                                      .collect(Collectors.toList())
                );
            }, this.dateNotBeforeProperty, this.dateNotAfterProperty, this.statsList);
        }
    }

    private static record DailyStat(LocalDate resultDate, int medianCpm, int averageCpm, int maxCpm) {}
}
