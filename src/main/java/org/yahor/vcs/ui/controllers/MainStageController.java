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
import javafx.util.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.yahor.vcs.ui.ApplicationRunner;
import org.yahor.vcs.ui.events.RepoAddedEvent;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.utils.FXUtils;
import org.yahor.vcs.ui.utils.Language;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class MainStageController implements Initializable, Observer {

    private static final String OPEN_DIALOG_FILE = "views/open_dialog.fxml";
    private static final String OPEN_DIALOG_TITLE = "dialog.open.title";

    private ResourceBundle bundle;

    @FXML private TabPane tabPane;

    @FXML
    public void open(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle(bundle.getString(OPEN_DIALOG_TITLE));
        Pair<Parent, Observable> sceneWithController = FXUtils.loadPaneWithController(OPEN_DIALOG_FILE, bundle);
        stage.setScene(new Scene(sceneWithController.getKey()));
        System.out.println(sceneWithController.getValue().countObservers());
        sceneWithController.getValue().addObserver(this);
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

    @Override
    public void update(Observable o, Object arg) {
        RepoAddedEvent event = (RepoAddedEvent) arg;
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(event.getRepoDir())
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
            Pair gridPaneWithController = FXUtils.loadPaneWithController("views/project_tab.fxml", bundle);
            Tab newTab = new Tab(event.getRepoName());
            GridPane gridPane = (GridPane) gridPaneWithController.getKey();
            gridPane.prefHeightProperty().bind(tabPane.heightProperty().add(-30));
            gridPane.prefWidthProperty().bind(tabPane.widthProperty());
            newTab.setContent(gridPane);
//            gridPaneWithController.getKey().
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
//            tabPane.set
            System.out.println("Added new tab");
            try {
                ((RepoTabController) gridPaneWithController.getValue()).loadRepo(new Repo(repository));
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
