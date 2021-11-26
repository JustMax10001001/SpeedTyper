package com.justsoft.speedtyper;

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

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

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
}
