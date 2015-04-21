package org.yahor.vcs.ui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.yahor.vcs.ui.ApplicationRunner;
import org.yahor.vcs.ui.utils.FXMLUtils;
import org.yahor.vcs.ui.utils.Language;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class MainStageController implements Initializable {

    private static final String OPEN_DIALOG_FILE = "views/open_dialog.fxml";
    private static final String OPEN_DIALOG_TITLE = "dialog.open.title";

    private ResourceBundle bundle;

    @FXML private TabPane tabPane;

    @FXML
    public void open(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle(bundle.getString(OPEN_DIALOG_TITLE));
        stage.setScene(new Scene(FXMLUtils.loadPane(OPEN_DIALOG_FILE, bundle)));
        stage.show();
    }

    @FXML
    public void close(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void changeLocale(ActionEvent event) throws IOException {
        MenuItem source = (MenuItem) event.getSource();
        ApplicationRunner.updateRoot(Language.getByLabelOrDefault(source.getText()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }
}
