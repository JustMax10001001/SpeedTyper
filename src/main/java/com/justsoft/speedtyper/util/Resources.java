package com.justsoft.speedtyper.util;

import com.justsoft.speedtyper.ui.controllers.ControllerWithParameters;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Resources {

    private Resources() {
    }

    private static URL getResourceUrl(String resourceName) {
        return Resources.class.getResource(resourceName);
    }

    public static FXMLLoader createLoaderForForm(String formName) {
        final URL resourceUrl = getResourceUrl(String.format("/forms/%s.fxml", formName));
        if (resourceUrl == null)
            throw new IllegalArgumentException(String.format("There is no form called \"%s\"", formName));
        return new FXMLLoader(resourceUrl);
    }

    public static FXMLLoader createLoaderForControl(String controlName) {
        final URL resourceUrl = getResourceUrl(String.format("/controls/%s.fxml", controlName));
        if (resourceUrl == null)
            throw new IllegalArgumentException(String.format("There is no control called \"%s\"", controlName));
        return new FXMLLoader(resourceUrl);
    }

    public static FXMLLoader createLoaderForForm(String formName, Bundle parameters) throws IOException {
        FXMLLoader loader = createLoaderForForm(formName);
        loader.load();

        if (loader.getController() instanceof ControllerWithParameters controller) {
            controller.setParametersAndInitialize(parameters);

            return loader;
        } else {
            throw new IllegalStateException(
                    String.format(
                            "Unable to set parameters for form \"%s\" as it does not extend ControllerWithParameters",
                            formName
                    )
            );
        }
    }

    public static Parent loadForm(String formName) throws IOException {
        return createLoaderForForm(formName).load();
    }

    public static InputStream getFileResourceAsStream(String resourceName) {
        final String resource = String.format("/files/%s", resourceName);
        return getResourceAsStream(resource);
    }

    private static InputStream getResourceAsStream(String resource) {
        InputStream resourceStream = Resources.class.getResourceAsStream(resource);
        if (resourceStream == null) {
            throw new IllegalArgumentException(String.format("There is no resource file called \"%s\"", resource));
        }
        return resourceStream;
    }
}
