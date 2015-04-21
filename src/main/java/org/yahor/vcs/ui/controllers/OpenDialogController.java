package org.yahor.vcs.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class OpenDialogController implements Initializable {

    @FXML private TextField workingCopyPath;
    @FXML private TextField repoName;

    @FXML
    public void browseRepo(ActionEvent event) {

    }

    @FXML
    public void confirmAddRepo(ActionEvent event) {

    }

    @FXML
    public void cancelAddRepo(ActionEvent event) {
        ((Stage) workingCopyPath.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
