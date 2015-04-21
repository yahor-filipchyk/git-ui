package org.yahor.vcs.ui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.yahor.vcs.ui.ApplicationRunner;
import org.yahor.vcs.ui.utils.Language;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class MainStageController implements Initializable {

    private ResourceBundle bundle;

    @FXML
    public void close(ActionEvent event) {
        Platform.exit();
    }

    @FXML void changeLocale(ActionEvent event) throws IOException {
        MenuItem source = (MenuItem) event.getSource();
        ApplicationRunner.updateRoot(Language.getByLabelOrDefault(source.getText()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }
}
