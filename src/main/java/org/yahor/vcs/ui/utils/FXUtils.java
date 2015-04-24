package org.yahor.vcs.ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public final class FXUtils {

    private FXUtils() {
    }

    public static Parent loadPane(String fxmlPath, ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        return loader.load(FXUtils.class.getClassLoader().getResourceAsStream(fxmlPath));
    }

    public static Pair<Parent, Observable> loadPaneWithController(String fxmlPath,
                                                                  ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        return new Pair<>(loader.load(FXUtils.class.getClassLoader().getResourceAsStream(fxmlPath)),
                loader.getController());
    }

    public static Stage getStage(Parent fxElement) {
        return (Stage) fxElement.getScene().getWindow();
    }

    public static void showMessage(Label label, String text, Color color) {
        label.setText(text);
        label.setTextFill(color);
    }

    public static Color getColor(ResourceBundle bundle, String key) {
        return Color.web(bundle.getString(key));
    }

    public static TreeItem<String> createTreeItemWithIcon(String label, Image icon, int imageHeight) {
        TreeItem<String> treeItem = new TreeItem<>(label);
        ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(imageHeight);
        treeItem.setGraphic(imageView);
        return treeItem;
    }
}
