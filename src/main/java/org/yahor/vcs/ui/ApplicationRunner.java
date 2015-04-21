package org.yahor.vcs.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.yahor.vcs.ui.utils.UTF8Control;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class ApplicationRunner extends Application {

    private static final String MAIN_STAGE_FILE = "views/main_stage.fxml";
    private static final String LANG_BUNDLE = "i18n.lang";
    private static final String WINDOW_TITLE = "window.title";

    private static ResourceBundle bundle;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        bundle = ResourceBundle.getBundle(LANG_BUNDLE, new UTF8Control());
        fxmlLoader.setResources(bundle);
        Parent root = fxmlLoader.load(getClass().getClassLoader().getResourceAsStream(MAIN_STAGE_FILE));
        primaryStage.setTitle(bundle.getString(WINDOW_TITLE));
        Dimension screensSize = Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setScene(new Scene(root, 0.9 * screensSize.getWidth(), 0.9 * screensSize.getHeight()));
        primaryStage.show();
    }

    public static ResourceBundle getResources() {
        return bundle;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
