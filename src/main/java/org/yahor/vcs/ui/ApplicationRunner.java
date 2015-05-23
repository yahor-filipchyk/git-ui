package org.yahor.vcs.ui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.yahor.vcs.ui.controllers.MainStageController;
import org.yahor.vcs.ui.controllers.ObservableController;
import org.yahor.vcs.ui.utils.Utils;
import org.yahor.vcs.ui.utils.UTF8Control;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class ApplicationRunner extends Application {

    private static final String MAIN_STAGE_FILE = "views/main_stage.fxml";
    private static final String LANG_BUNDLE = "i18n.lang";
    private static final String WINDOW_TITLE = "window.title";

    private static ResourceBundle bundle;
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationRunner.primaryStage = primaryStage;
        updateRoot(Locale.getDefault());
        primaryStage.setOnCloseRequest(event -> {
//           FXUtils.loadPane()
        });
        primaryStage.show();
    }

    public static void updateRoot(Locale locale) throws IOException {
        try {
            bundle = ResourceBundle.getBundle(LANG_BUNDLE, locale, new UTF8Control());
            GridPane root = (GridPane) Utils.loadPane(MAIN_STAGE_FILE, bundle);
            primaryStage.setTitle(bundle.getString(WINDOW_TITLE));
            Dimension screensSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 0.9 * screensSize.getWidth(), 0.9 * screensSize.getHeight()));
            } else {
                primaryStage.getScene().setRoot(root);
            }
        } catch (MissingResourceException ex) {
            // TODO update status bar with error message
        }
    }

    public static ResourceBundle getResources() {
        return bundle;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
