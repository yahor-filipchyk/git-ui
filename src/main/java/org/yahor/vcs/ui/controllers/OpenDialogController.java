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
import org.yahor.vcs.ui.events.RepoCreatedEvent;
import org.yahor.vcs.ui.events.RepoEvent;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.utils.Utils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static org.codehaus.plexus.util.StringUtils.isNotEmpty;
import static org.yahor.vcs.ui.utils.Utils.showMessage;

/**
 * @author yahor-filipchyk
 */
public class OpenDialogController extends BaseDialogController {

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
    @FXML private TextField newRepoDestinationPath;
    @FXML private TextField creatingRepoName;
    @FXML private Label createStatusLabel;
    @FXML private Button createRepoBtn;

    private ResourceBundle bundle;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private File workingCopyDir;
    private File cloningDestinationDir;
    private File creatingDestinationDir;

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
        File dir = directoryChooser.showDialog(Utils.getStage(destinationPath));
        if (dir == null) {
            return;
        }
        destinationPath.setText(dir.getAbsolutePath());
        cloningDestinationDir = dir;
        updateLabelMessage(cloneStatusLabel, "Repo will be saved to folder " + dir.getName(), getColor(COLOR_SUCCESS));
        if (isNotEmpty(url.getText())) {
            cloneRepoBtn.setDisable(false);
        }
    }

    @FXML
    public void browseRepo(ActionEvent event) {
        directoryChooser.setTitle(bundle.getString(FOLDER_CHOOSER_TITLE));
        File dir = directoryChooser.showDialog(Utils.getStage(workingCopyPath));
        if (dir == null) {
            return;
        }
        workingCopyPath.setText(dir.getAbsolutePath());
        Path gitFolder = Paths.get(dir.getAbsolutePath(), ".git");
        if (!Files.exists(gitFolder)) {
            updateLabelMessage(openStatusLabel, bundle.getString(DIALOG_STATUS_NO_REPO), getColor(COLOR_ERROR));
        } else if (!RepositoryCache.FileKey.isGitRepository(gitFolder.toFile(), FS.detect())) {
            updateLabelMessage(openStatusLabel, bundle.getString(DIALOG_STATUS_NOT_VALID_REPO), getColor(COLOR_ERROR));
        } else {    // success
            updateLabelMessage(openStatusLabel, bundle.getString(DIALOG_STATUS_HAS_REPO), getColor(COLOR_SUCCESS));
            addingRepoName.setText(dir.getName());
            openRepoBtn.setDisable(false);
            workingCopyDir = gitFolder.toFile();
        }
    }

    @FXML public void browseNewRepoPath(ActionEvent event) {
        File dir = directoryChooser.showDialog(Utils.getStage(newRepoDestinationPath));
        if (dir == null) {
            return;
        }
        newRepoDestinationPath.setText(dir.getAbsolutePath());
        creatingRepoName.setText(dir.getName());
        updateLabelMessage(createStatusLabel, "Repo will be saved to folder " + dir.getName(), getColor(COLOR_SUCCESS));
        createRepoBtn.setDisable(false);
        creatingDestinationDir = dir;
    }

    @Override
    public void submit(ActionEvent event) {
        Button caller = (Button) event.getSource();
        RepoEvent repoEvent = null;
        if (caller == cloneRepoBtn) {
            if (cloningDestinationDir != null && isNotEmpty(url.getText())) {
                repoEvent = new RepoClonedEvent(cloningDestinationDir, url.getText(), cloningRepoName.getText());
            }
        } else if (caller == createRepoBtn) {
            if (creatingDestinationDir != null) {
                repoEvent = new RepoCreatedEvent(creatingDestinationDir, creatingRepoName.getText());
            }
        } else if (caller == openRepoBtn) {
            if (workingCopyDir != null) {
                repoEvent = new RepoAddedEvent(workingCopyDir, addingRepoName.getText());
            }
        }
        if (repoEvent != null) {
            notifyListeners(repoEvent);
            Utils.getStage(destinationPath).close();
        }
    }

    @Override
    public void cancel(ActionEvent event) {
        Utils.getStage(workingCopyPath).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.url.textProperty().addListener(this::urlChanged);
    }

    private void urlChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (isNotEmpty(newValue)) {
            cloningRepoName.setText(Repo.repoName(newValue));
        }
    }

    private void updateLabelMessage(Label label, String message, Color color) {
        showMessage(label, message, color);
    }

    private Color getColor(String key) {
        return Utils.getColor(bundle, key);
    }
}
