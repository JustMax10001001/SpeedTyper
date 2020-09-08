package com.justsoft.speedtyper.ui.controls;

import com.justsoft.speedtyper.ui.dialogs.ExceptionDialog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Timer extends VBox {

    private static final int DEFAULT_TIMER_VALUE = 60;

    @FXML
    private Label timeLabel;

    private final ReadOnlyIntegerWrapper timeRemainingProperty = new ReadOnlyIntegerWrapper(DEFAULT_TIMER_VALUE);

    @FXML
    private int timerLength = DEFAULT_TIMER_VALUE;

    private volatile boolean paused = false;

    private Service<Void> timerService;

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
        timerService = constructTimerService();

        timerService.setOnFailed(event -> {
            timerService.getException().printStackTrace();
            timeRemainingProperty.set(timerLength);
        });
        timerService.setOnCancelled(event -> {
            System.out.println("Timer cancelled");
            timeRemainingProperty.set(timerLength);
        });
        timerService.setOnSucceeded(event -> timeRemainingProperty.set(timerLength));
        timeRemainingProperty.addListener((observable, oldValue, newValue) -> {
            updateLabel(newValue.intValue());
        });
    }

    public void start() {
        if (timerService.isRunning())
            throw new IllegalStateException("Timer is already running");

        timerService.reset();
        timerService.start();
    }

    public boolean cancel() {
        return timerService.cancel();
    }

    public void resume() {
        paused = false;
    }

    public void pause() {
        if (!timerService.isRunning())
            throw new IllegalStateException("Timer is not running");
        paused = true;
    }

    public void setOnFinished(TimerFinishedEventHandler handler) {
        timerService.setOnSucceeded(event -> {
            handler.onFinished();
            timeRemainingProperty.set(timerLength);
        });
    }

    private void updateLabel(int newValue) {
        Platform.runLater(() -> timeLabel.setText(String.format("%02d:%02d", newValue / 60, newValue % 60)));
    }

    private Service<Void> constructTimerService() {
        return new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return constructTimerTask();
            }
        };
    }


    private Task<Void> constructTimerTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                timeRemainingProperty.set(timerLength);
                paused = false;
                while (timeRemainingProperty.get() > 0 && !this.isCancelled()) {
                    if (!paused) {
                        timeRemainingProperty.set(timeRemainingProperty.get() - 1);
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(0, 10000);
                    }
                }
                return null;
            }
        };
    }

    public ReadOnlyIntegerProperty timeRemainingProperty() {
        return timeRemainingProperty.getReadOnlyProperty();
    }

    /**
     * Sets timer length
     *
     * @param timerLength - timer delay in seconds
     */
    public void setTimerLength(int timerLength) {
        if (timerService.isRunning())
            throw new IllegalStateException("Timer is currently running!");
        this.timerLength = timerLength;
        updateLabel(timerLength);
    }

    public int getTimerLength() {
        return timerLength;
    }

    public boolean isRunning() {
        return timerService.isRunning();
    }

    public interface TimerFinishedEventHandler {
        void onFinished();
    }
}
