package org.yahor.vcs.ui.controllers;

import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.yahor.vcs.ui.ApplicationRunner;
import org.yahor.vcs.ui.events.BranchCheckedOutEvent;
import org.yahor.vcs.ui.events.RepoAddedEvent;
import org.yahor.vcs.ui.events.RepoClonedEvent;
import org.yahor.vcs.ui.events.RepoCreatedEvent;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.services.RepoService;
import org.yahor.vcs.ui.utils.Utils;
import org.yahor.vcs.ui.utils.Language;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author yahor-filipchyk
 */
public class MainStageController implements Initializable {

    private static final String OPEN_DIALOG_FILE = "views/open_dialog.fxml";
    private static final String CHECKOUT_DIALOG_FILE = "views/checkout_dialog.fxml";
    private static final String PROJECT_TAB_FILE = "views/project_tab.fxml";
    private static final String OPEN_DIALOG_TITLE = "dialog.open.title";

    private ResourceBundle bundle;
    private Map<Tab, Pair<RepoService, RepoTabController>> repos = Maps.newHashMap();

    @FXML private TabPane tabPane;
    @FXML private ToolBar toolbar;
    @FXML private Label statusBar;

    @FXML
    public void open(ActionEvent event) throws IOException {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(bundle.getString(OPEN_DIALOG_TITLE));
        Pair<Parent, ObservableController> sceneWithController = Utils.loadPaneWithController(OPEN_DIALOG_FILE, bundle);
        stage.setScene(new Scene(sceneWithController.getKey()));
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
        ((OpenDialogController) sceneWithController.getValue()).baseHeight = stage.getHeight();
        sceneWithController.getValue().addListener(RepoAddedEvent.class, this::addRepo);
        sceneWithController.getValue().addListener(RepoClonedEvent.class, this::cloneRepo);
        sceneWithController.getValue().addListener(RepoCreatedEvent.class, this::createRepo);
    }

    @FXML public void checkout(ActionEvent event) throws IOException {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle("Checkout branch");
        Pair<Parent, ObservableController> sceneWithController = Utils.loadPaneWithController(CHECKOUT_DIALOG_FILE, bundle);
        stage.setScene(new Scene(sceneWithController.getKey()));
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
        sceneWithController.getValue().addListener(BranchCheckedOutEvent.class, this::checkoutBranch);
        RepoService currentRepo = getCurrentRepo();
        ((CheckoutDialogController) sceneWithController.getValue()).updateBranchesList(
                currentRepo.repoDir(),
                currentRepo.repo().allBranchesSorted(),
                currentRepo.repo().remotes());
    }

    @FXML
    public void close(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void changeLocale(ActionEvent event) throws IOException {
        MenuItem source = (MenuItem) event.getSource();
        ApplicationRunner.updateRoot(Language.getByLabelOrDefault(source.getText()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }

    private void cloneRepo(RepoClonedEvent event) {
        RepoService repoService = RepoService.cloneRepo(event.url(), event.destinationDir(), event.repoName());
        addNewTab(repoService);
    }

    private void addRepo(RepoAddedEvent event) {
        RepoService repoService = RepoService.openRepo(event.repoDir(), event.repoName());
        addNewTab(repoService);
    }

    private void createRepo(RepoCreatedEvent event) {
        RepoService repoService = RepoService.createRepo(event.repoDir(), event.repoName());
        addNewTab(repoService);
    }

    private void checkoutBranch(BranchCheckedOutEvent event) {
        getCurrentRepo().checkoutBranch(event.remoteBranch(), event.localBranch(), event.track());
        updateCurrentRepo();
    }

    private void updateCurrentRepo() {
        Platform.runLater(() -> {
            final RepoService currentRepo = getCurrentRepo();
            getCurrentTabController().loadRepo(getCurrentRepo());
            statusBar.setText(currentRepo.currentBranch());
        });
    }

    private void disableInstruments() {
        toolbar.getItems().stream()
                .skip(1)
                .forEach(item -> item.setDisable(true));
    }

    private void enableInstruments() {
        toolbar.getItems().forEach(item -> item.setDisable(false));
    }

    private RepoService getCurrentRepo() {
        return repos.get(tabPane.getTabs().filtered(Tab::isSelected).get(0)).getKey();
    }

    private RepoTabController getCurrentTabController() {
        return repos.get(tabPane.getTabs().filtered(Tab::isSelected).get(0)).getValue();
    }

    private void addNewTab(RepoService repoService) {
        try {
            Pair gridPaneWithController = Utils.loadPaneWithController(PROJECT_TAB_FILE, bundle);
            Tab newTab = new Tab(repoService.name());
            newTab.setOnCloseRequest(closeEvent -> {
                // TODO implement showing confirmation dialog

                repos.remove(newTab);
                // latest is closing
                if (tabPane.getTabs().size() == 1 && !closeEvent.isConsumed()) {
                    disableInstruments();
                }
            });
            newTab.setOnClosed(closedEvent -> repoService.close());
            GridPane gridPane = (GridPane) gridPaneWithController.getKey();
            gridPane.prefHeightProperty().bind(tabPane.heightProperty().add(-30));
            gridPane.prefWidthProperty().bind(tabPane.widthProperty());
            newTab.setContent(gridPane);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
            statusBar.setText(repoService.currentBranch());
            RepoTabController repoTabController = (RepoTabController) gridPaneWithController.getValue();
            repoTabController.loadRepo(repoService);
            repos.put(newTab, new Pair<>(repoService, repoTabController));
        } catch (IOException e) {
            e.printStackTrace();
        }
        enableInstruments();
    }
}
