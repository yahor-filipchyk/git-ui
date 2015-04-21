package org.yahor.vcs.ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public final class FXMLUtils {

    private FXMLUtils() {}

    public static Parent loadPane(String fxmlPath, ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        return loader.load(FXMLUtils.class.getClassLoader().getResourceAsStream(fxmlPath));
    }
}
