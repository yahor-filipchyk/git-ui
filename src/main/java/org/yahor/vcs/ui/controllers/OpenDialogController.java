package org.yahor.vcs.ui.controllers;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.yahor.vcs.ui.events.RepoAddedEvent;
import org.yahor.vcs.ui.events.RepoClonedEvent;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.utils.FXUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;
import static org.yahor.vcs.ui.utils.FXUtils.showMessage;

/**
 * @author yahor-filipchyk
 */
public class OpenDialogController extends ObservableController implements Initializable {

    private static final String FOLDER_CHOOSER_TITLE = "dialog.choose_repo.title";
    private static final String DIALOG_STATUS_NO_REPO = "dialog.status.no_repo";
    private static final String DIALOG_STATUS_NOT_VALID_REPO = "dialog.status.not_valid_repo";
    private static final String DIALOG_STATUS_HAS_REPO = "dialog.status.has_repo";
    private static final String COLOR_ERROR = "color.error";
    private static final String COLOR_SUCCESS = "color.success";

    // Clone tab
    @FXML private Tab tabClone;
    @FXML private TextField destinationPath;
    @FXML private TextField url;
    @FXML private TextField cloningRepoName;
    @FXML private Label cloneStatusLabel;
    @FXML private Button cloneRepoBtn;

    // Open tab
    @FXML private Tab tabOpen;
    @FXML private TextField workingCopyPath;
    @FXML private TextField addingRepoName;
    @FXML private Label openStatusLabel;
    @FXML private Button openRepoBtn;

    // Create tab
    @FXML private Tab tabCreate;

    private ResourceBundle bundle;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private File workingCopyDir;
    private File cloningDestinationDir;

    double baseHeight;

    @FXML void addingTypeChanged(Event event) {
        Tab caller = (Tab) event.getSource();
        if (caller.isSelected() && caller.getTabPane() != null) {
            Window window = caller.getTabPane().getScene().getWindow();
            double heightToSet = window.getHeight();
            if (caller == tabClone) {
                heightToSet = baseHeight;
            } else if (caller == tabOpen) {
                heightToSet = baseHeight - 35;
            } else if (caller == tabCreate) {
                heightToSet = baseHeight - 35;
            }
            if (heightToSet != window.getHeight()) {
                window.setHeight(heightToSet);
            }
        }
    }

    @FXML
    public void browseTargetPath(ActionEvent event) {
        File dir = directoryChooser.showDialog(FXUtils.getStage(workingCopyPath));
        if (dir == null) {
            return;
        }
        destinationPath.setText(dir.getAbsolutePath());
        cloningDestinationDir = dir;
        updateCloneStatus("Repo will be saved to folder " + dir.getName(), getColor(COLOR_SUCCESS));
        if (isNotEmpty(url.getText())) {
            cloneRepoBtn.setDisable(false);
        }
    }

    @FXML
    public void browseRepo(ActionEvent event) {
        directoryChooser.setTitle(bundle.getString(FOLDER_CHOOSER_TITLE));
        File dir = directoryChooser.showDialog(FXUtils.getStage(workingCopyPath));
        if (dir == null) {
            return;
        }
        workingCopyPath.setText(dir.getAbsolutePath());
        Path gitFolder = Paths.get(dir.getAbsolutePath(), ".git");
        if (!Files.exists(gitFolder)) {
            updateOpenStatus(bundle.getString(DIALOG_STATUS_NO_REPO), getColor(COLOR_ERROR));
        } else if (!RepositoryCache.FileKey.isGitRepository(gitFolder.toFile(), FS.detect())) {
            updateOpenStatus(bundle.getString(DIALOG_STATUS_NOT_VALID_REPO), getColor(COLOR_ERROR));
        } else {    // success
            updateOpenStatus(bundle.getString(DIALOG_STATUS_HAS_REPO), getColor(COLOR_SUCCESS));
            addingRepoName.setText(dir.getName());
            openRepoBtn.setDisable(false);
            workingCopyDir = gitFolder.toFile();
        }
    }

    @FXML
    public void confirmCloneRepo(ActionEvent event) {
        if (cloningDestinationDir != null && isNotEmpty(url.getText())) {
            Stage currentStage = FXUtils.getStage(destinationPath);
            notifyListeners(new RepoClonedEvent(url.getText(), cloningDestinationDir, cloningRepoName.getText()));
            currentStage.close();
        }
    }

    @FXML
    public void confirmAddRepo(ActionEvent event) {
        if (workingCopyDir != null) {
            Stage currentStage = FXUtils.getStage(workingCopyPath);
            notifyListeners(new RepoAddedEvent(workingCopyDir, addingRepoName.getText()));
            currentStage.close();
        }
    }

    @FXML
    public void cancelAddRepo(ActionEvent event) {
        FXUtils.getStage(workingCopyPath).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.url.textProperty().addListener(this::urlChanged);
    }

    public void urlChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (isNotEmpty(newValue)) {
            if (newValue.contains("/")) {
                cloningRepoName.setText(Repo.repoName(newValue));
            }
        }
    }

    private void updateOpenStatus(String message, Color color) {
        showMessage(openStatusLabel, message, color);
    }

    private void updateCloneStatus(String message, Color color) {
        showMessage(cloneStatusLabel, message, color);
    }

    private Color getColor(String key) {
        return FXUtils.getColor(bundle, key);
    }
}
