package org.yahor.vcs.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @author yahor-filipchyk
 */
public class ApplicationRunner extends Application {

    private static final String MAIN_STAGE_FILE = "main_stage.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(MAIN_STAGE_FILE));
        primaryStage.setTitle("Git GUI");
        Dimension screensSize = Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setScene(new Scene(root, 0.9 * screensSize.getWidth(), 0.9 * screensSize.getHeight()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
