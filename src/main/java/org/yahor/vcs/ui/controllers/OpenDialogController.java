package org.yahor.vcs.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.yahor.vcs.ui.events.RepoAddedEvent;
import org.yahor.vcs.ui.utils.FXUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.ResourceBundle;

import static org.yahor.vcs.ui.utils.FXUtils.showMessage;

/**
 * @author yahor-filipchyk
 */
public class OpenDialogController extends Observable implements Initializable {

    private static final String FOLDER_CHOOSER_TITLE = "dialog.choose_repo.title";
    private static final String DIALOG_STATUS_NO_REPO = "dialog.status.no_repo";
    private static final String DIALOG_STATUS_NOT_VALID_REPO = "dialog.status.not_valid_repo";
    private static final String DIALOG_STATUS_HAS_REPO = "dialog.status.has_repo";
    private static final String COLOR_ERROR = "color.error";
    private static final String COLOR_SUCCESS = "color.success";

    @FXML private Label openStatusLabel;
    @FXML private TextField workingCopyPath;
    @FXML private TextField repoName;
    @FXML private Button openRepoBtn;

    private ResourceBundle bundle;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private File chosenDir;

    @FXML
    public void browseRepo(ActionEvent event) throws IOException {
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
            repoName.setText(dir.getName());
            openRepoBtn.setDisable(false);
            chosenDir = gitFolder.toFile();
        }
    }

    @FXML
    public void confirmAddRepo(ActionEvent event) throws IOException {
        if (chosenDir != null) {
            Stage currentStage = FXUtils.getStage(workingCopyPath);
            setChanged();
            notifyObservers(new RepoAddedEvent() {
                @Override
                public File getRepoDir() {
                    return chosenDir;
                }

                @Override
                public String getRepoName() {
                    return repoName.getText();
                }
            });
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
        directoryChooser.setTitle(resources.getString(FOLDER_CHOOSER_TITLE));
    }

    private void updateOpenStatus(String message, Color color) {
        showMessage(openStatusLabel, message, color);
    }

    private Color getColor(String key) {
        return FXUtils.getColor(bundle, key);
    }
}
