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
import org.yahor.vcs.ui.controllers.ObservableController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author yahor-filipchyk
 */
public final class Utils {

    private Utils() {
    }

    public static Parent loadPane(String fxmlPath, ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        return loader.load(Utils.class.getClassLoader().getResourceAsStream(fxmlPath));
    }

    public static Pair<Parent, ObservableController> loadPaneWithController(String fxmlPath,
                                                                  ResourceBundle resources) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);
        return new Pair<>(loader.load(Utils.class.getClassLoader().getResourceAsStream(fxmlPath)),
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

    public static TreeItem<String> createTreeItemWithIcon(String label, Image icon, int imageHeight, boolean expanded) {
        TreeItem<String> treeItem = new TreeItem<>(label);
        ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(imageHeight);
        treeItem.setGraphic(imageView);
        treeItem.setExpanded(expanded);
        return treeItem;
    }

    public static ImageView createImageView(Image icon, int height) {
        ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
        return imageView;
    }

    @SafeVarargs
    public static <T> List<T> concatLists(List<T> list1, List<T> list2, List<T> ... otherLists) {
        requireNonNull(list1);
        requireNonNull(list2);
        Stream<T> combining = Stream.concat(list1.stream(), list2.stream());
        for (List<T> list : otherLists) {
            combining = Stream.concat(combining, list.stream());
        }
        return combining.collect(Collectors.toList());
    }

    public static String getFileContents(Path file) throws IOException {
        return new String(Files.readAllBytes(file), Charset.forName("utf-8"));
    }
}
