package com.justsoft.speedtyper;

import com.justsoft.speedtyper.model.entities.TypingSessionResult;
import com.justsoft.speedtyper.repositories.SessionResultsRepository;
import com.justsoft.speedtyper.services.SessionResultMapService;
import com.justsoft.speedtyper.ui.controllers.MainController;
import com.justsoft.speedtyper.ui.dialogs.ExceptionAlert;
import com.justsoft.speedtyper.util.Resources;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.time.LocalDate;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainController controller;
        Parent root;
        {
            FXMLLoader loader;
            try {
                loader = Resources.createLoaderForForm("main_form");
            } catch (Exception e) {
                ExceptionAlert.show(e, "Unable to load main form");
                Platform.exit();
                return;
            }
            root = loader.load();
            controller = loader.getController();
        }

        primaryStage.setTitle("Typing speed test");

        Scene scene = new Scene(root, -1, -1);
        primaryStage.setScene(scene);

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, controller::keyPressed);
        primaryStage.show();
    }


    public static void main(String[] args) {
        createFakeData();
        launch(args);
    }

    private static void createFakeData() {
        if (SessionResultsRepository.getPreferredInstance() instanceof SessionResultMapService) {
            SessionResultsRepository.getPreferredInstance().save(
                    new TypingSessionResult(240, 40, 2, LocalDate.of(2020, 2, 5), 60)
            );
            SessionResultsRepository.getPreferredInstance().save(
                    new TypingSessionResult(200, 30, 5, LocalDate.of(2020, 2, 7), 60)
            );
            SessionResultsRepository.getPreferredInstance().save(
                    new TypingSessionResult(220, 32, 1, LocalDate.of(2020, 2, 8), 60)
            );
            SessionResultsRepository.getPreferredInstance().save(
                    new TypingSessionResult(280, 50, 0, LocalDate.of(2020, 2, 10), 60)
            );
        }
    }
}
