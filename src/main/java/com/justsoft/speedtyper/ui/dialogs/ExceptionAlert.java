package com.justsoft.speedtyper.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAlert {

    public static void show(Throwable exception, String message, String title, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setHeaderText(header);
        alert.setTitle(title);
        if (exception != null) {
            exception.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea exceptionArea = new TextArea();
            exceptionArea.setWrapText(true);
            exceptionArea.setEditable(false);
            exceptionArea.setText(exceptionText);

            GridPane.setVgrow(exceptionArea, Priority.ALWAYS);
            GridPane.setHgrow(exceptionArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(exceptionArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
        }
        alert.showAndWait();
    }

    public static void show(Throwable exception, String message, String title){
        show(exception, message, title, null);
    }

    public static void show(Throwable ex, String message){
        show(ex, message, "Error occurred", null);
    }

    public static void show(Throwable ex){
        show(ex, "An error occurred", "Error occurred", null);
    }
}
