package org.yahor.vcs.ui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.yahor.vcs.ui.events.BranchCheckedOutEvent;
import org.yahor.vcs.ui.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * @author yahor-filipchyk
 */
public class CheckoutDialogController extends BaseDialogController {

    @FXML private ComboBox branches;
    @FXML private TextField newBranchName;
    @FXML private CheckBox trackRemote;
    private File repoDir;

    @Override
    public void submit(ActionEvent event) {
        // TODO add branch existence checking
        notifyListeners(new BranchCheckedOutEvent(repoDir,
                branches.getSelectionModel().getSelectedItem().toString(),
                newBranchName.getText(),
                trackRemote.isSelected()));
        Utils.getStage(branches).close();
    }

    @Override
    public void cancel(ActionEvent event) {
        Utils.getStage(branches).close();
    }

    @SuppressWarnings("unchecked")
    void updateBranchesList(File repoDir, List<String> branches, Set<String> remotes) {
        this.repoDir = repoDir;
        this.branches.setItems(FXCollections.observableArrayList(branches));
        this.branches.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String branch = String.valueOf(newValue);
            if (StringUtils.isNotEmpty(branch)) {
                for (String remote : remotes) {
                    if (branch.startsWith(remote)) {
                        branch = branch.substring(remote.length() + 1);
                        break;
                    }
                }
                newBranchName.setText(String.valueOf(branch));
            }
        });
    }
}
