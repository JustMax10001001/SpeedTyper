package com.justsoft.speedtyper.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Resources {

    private static final Resources instance;

    private Resources() {
    }

    static {
        //noinspection InstantiationOfUtilityClass
        instance = new Resources();
    }

    public static Parent loadForm(String formName) throws IOException {
        return FXMLLoader.load(instance.getClass().getResource("forms/" + formName + ".fxml"));
    }

    public static File loadFile(String fileName) throws URISyntaxException {
        return new File(instance.getClass().getResource("files/" + fileName).toURI());
    }
}
