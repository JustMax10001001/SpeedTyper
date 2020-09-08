package com.justsoft.speedtyper.ui.controls;

import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.*;

public class Timer extends VBox {

    private static final int DEFAULT_TIMER_VALUE = 60;

    @FXML
    private Label timeLabel;

    private final ReadOnlyIntegerWrapper timeRemainingProperty = new ReadOnlyIntegerWrapper(DEFAULT_TIMER_VALUE);
    private final ReadOnlyBooleanWrapper isPausedProperty = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper isRunningProperty = new ReadOnlyBooleanWrapper(false);

    private final ScheduledExecutorService timerTaskExecutor = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private int timerLength = DEFAULT_TIMER_VALUE;

    //private volatile boolean paused = false;

    private ScheduledFuture<?> timerScheduleHandle;

    private TimerFinishedEventHandler onFinishedEventHandler;

    public Timer() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/justsoft/speedtyper/resources/controls/timer_control.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            ExceptionDialog.show(e, "Unable to load timer fxml");
        }
    }

    @FXML
    private void initialize() {
        this.getStyleClass().add("timer");

        timeLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%02d:%02d", timeRemainingProperty.get() / 60, timeRemainingProperty.get() % 60),
                        timeRemainingProperty
                )
        );
    }

    private void timerTick() {
        if (isPausedProperty.get())
            return;

        if (timeRemainingProperty.get() > 0) {
            Platform.runLater(() ->
                timeRemainingProperty.set(timeRemainingProperty.get() - 1)
            );
        } else {
            if (onFinishedEventHandler != null)
                Platform.runLater(() -> {
                    isRunningProperty.set(false);
                    isPausedProperty.set(false);
                    onFinishedEventHandler.onFinished();
                });
            cancel();
        }
    }

    public void start() {
        timeRemainingProperty.set(timerLength);
        isRunningProperty.set(true);
        isPausedProperty.set(false);

        timerScheduleHandle = timerTaskExecutor.scheduleAtFixedRate(this::timerTick, 0, 1, TimeUnit.SECONDS);
    }

    public boolean cancel() {
        return timerScheduleHandle.cancel(false);
    }

    public void setOnFinished(TimerFinishedEventHandler handler) {
        this.onFinishedEventHandler = handler;
    }

    public ReadOnlyIntegerProperty timeRemainingProperty() {
        return timeRemainingProperty.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty isRunningProperty() {
        return isRunningProperty.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty isPausedProperty() {
        return isPausedProperty.getReadOnlyProperty();
    }

    public void setTimerLength(int timerLength) {
        this.timerLength = timerLength;
    }

    public int getTimerLength() {
        return timerLength;
    }

    public boolean isRunning() {
        return isRunningProperty.get();
    }

    public interface TimerFinishedEventHandler {
        void onFinished();
    }
}
