package org.yahor.vcs.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public abstract class BaseDialogController extends ObservableController implements Initializable {

    private ResourceBundle bundle;

    protected ResourceBundle getResources() {
        return bundle;
    }

    @FXML
    public abstract void submit(ActionEvent event);

    @FXML
    public abstract void cancel(ActionEvent event);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }
}
