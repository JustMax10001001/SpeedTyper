package com.justsoft.speedtyper.util;

import com.justsoft.speedtyper.ui.controllers.ControllerWithParameters;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Resources {

    private Resources() {
    }

    private static URL getResourceUrl(String resourceName) {
        return Resources.class.getResource(resourceName);
    }

    public static FXMLLoader createLoaderForForm(String formName) {
        final URL resourceUrl = getResourceUrl(String.format("/res/forms/%s.fxml", formName));
        if (resourceUrl == null)
            throw new IllegalArgumentException(String.format("There is no form called \"%s\"", formName));
        return new FXMLLoader(resourceUrl);
    }

    public static FXMLLoader createLoaderForFormWithParameters(String formName, Bundle parameters) throws IOException {
        FXMLLoader loader = createLoaderForForm(formName);
        loader.load();
        Object controller = loader.getController();
        if (!(controller instanceof ControllerWithParameters))
            throw new IllegalStateException(
                    String.format(
                            "Unable to set parameters for controller %s as it does not extend ControllerWithParameters",
                            controller.getClass().getName()
                    )
            );
        ((ControllerWithParameters) controller).setParametersAndInitialize(parameters);
        return loader;
    }

    public static Parent loadForm(String formName) throws IOException {
        return createLoaderForForm(formName).load();
    }

    public static File loadFile(String fileName) throws URISyntaxException {
        final URL resourceUrl = getResourceUrl(String.format("/res/files/%s", fileName));
        if (resourceUrl == null)
            throw new IllegalArgumentException(String.format("There is no file called \"%s\"", fileName));
        return new File(resourceUrl.toURI());
    }
}
