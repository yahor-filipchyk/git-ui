package org.yahor.vcs.ui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.yahor.vcs.ui.ApplicationRunner;
import org.yahor.vcs.ui.events.RepoAddedEvent;
import org.yahor.vcs.ui.events.RepoClonedEvent;
import org.yahor.vcs.ui.events.RepoCreatedEvent;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.utils.FXUtils;
import org.yahor.vcs.ui.utils.Language;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class MainStageController implements Initializable {

    private static final String OPEN_DIALOG_FILE = "views/open_dialog.fxml";
    private static final String PROJECT_TAB_FILE = "views/project_tab.fxml";
    private static final String OPEN_DIALOG_TITLE = "dialog.open.title";

    private ResourceBundle bundle;

    @FXML private TabPane tabPane;

    @FXML
    public void open(ActionEvent event) throws IOException {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(bundle.getString(OPEN_DIALOG_TITLE));
        Pair<Parent, ObservableController> sceneWithController = FXUtils.loadPaneWithController(OPEN_DIALOG_FILE, bundle);
        stage.setScene(new Scene(sceneWithController.getKey()));
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
        ((OpenDialogController) sceneWithController.getValue()).baseHeight = stage.getHeight();
        sceneWithController.getValue().addListener(RepoAddedEvent.class, this::addRepo);
        sceneWithController.getValue().addListener(RepoClonedEvent.class, this::cloneRepo);
        sceneWithController.getValue().addListener(RepoCreatedEvent.class, this::createRepo);
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

    private void cloneRepo(RepoClonedEvent event) {
        Repo repo = Repo.cloneRepo(event.url(), event.destinationDir());
        addNewTab(repo, event.repoName());
    }

    private void addRepo(RepoAddedEvent event) {
        Repo repo = Repo.openRepo(event.repoDir());
        addNewTab(repo, event.repoName());
    }

    private void createRepo(RepoCreatedEvent event) {
        Repo repo = Repo.createRepo(event.repoDir());
        addNewTab(repo, event.repoName());
    }

    private void addNewTab(Repo repo, String repoName) {
        try {
            Pair gridPaneWithController = FXUtils.loadPaneWithController(PROJECT_TAB_FILE, bundle);
            Tab newTab = new Tab(repoName);
            newTab.setOnCloseRequest(closeEvent -> {
                // TODO implement showing confirmation dialog
            });
            newTab.setOnClosed(closedEvent -> repo.close());
            GridPane gridPane = (GridPane) gridPaneWithController.getKey();
            gridPane.prefHeightProperty().bind(tabPane.heightProperty().add(-30));
            gridPane.prefWidthProperty().bind(tabPane.widthProperty());
            newTab.setContent(gridPane);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
            ((RepoTabController) gridPaneWithController.getValue()).loadRepo(repo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
