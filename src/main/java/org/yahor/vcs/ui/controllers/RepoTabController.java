package org.yahor.vcs.ui.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.model.File;
import org.yahor.vcs.ui.services.RepoService;
import org.yahor.vcs.ui.utils.Utils;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author yfilipchyk
 */
public class RepoTabController implements Initializable {

    @FXML private TreeView<String> refs;

    @FXML private TableView<File> workingCopyFiles;
    @FXML private TableColumn<File, ImageView> workingCopyIcon;
    @FXML private TableColumn<File, String> workingCopyPath;

    @FXML private TableView<File> indexFiles;
    @FXML private TableColumn<File, ImageView> indexFileIcon;
    @FXML private TableColumn<File, String> indexFilePath;
    @FXML private WebView diffView;

    private RepoService repoService;

    private final Image tagIcon = getImage("images/tag.png");
    private final Image branchIcon = getImage("images/branch.png");
    private final Image remoteIcon = getImage("images/remote.png");
    private final Image singleRemoteIcon = getImage("images/server.png");
    private final Image localIcon = getImage("images/local.png");
    private final Image tagsIcon = getImage("images/tags.png");
    private final Image folderIcon = getImage("images/folder.png");

    private final Image editedIcon = getImage("images/edited.png");
    private final Image untrackedIcon = getImage("images/untracked.png");

    public void loadRepo(RepoService repoService) {
        this.repoService = repoService;
        showBranches();
        showFileStatuses();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workingCopyPath.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        workingCopyIcon.setCellValueFactory(new PropertyValueFactory<>("icon"));

        indexFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        indexFileIcon.setCellValueFactory(new PropertyValueFactory<>("icon"));
    }

    private final Callback<TreeView<String>, TreeCell<String>> refsCellFactory = tv ->
            new TreeCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(getItem() != null ? getItem() : "");
                        setGraphic(getTreeItem().getGraphic());
                        TreeItem<String> current = getTreeItem();
                        if (repoService.currentBranch().contains(current.getValue())) {
                            TreeItem<String> parent = getTreeItem().getParent();
                            String currentBranch = current.getValue();
                            while (parent != null &&
                                    (!Repo.Branches().equals(parent.getValue()))) {
                                currentBranch = parent.getValue() + "/" + currentBranch;
                                parent = parent.getParent();
                            }
                            if (currentBranch.equals(repoService.currentBranch())) {
                                getStyleClass().add("active-branch");
                            }
                        } else {
                            getStyleClass().remove("active-branch");
                        }
                        setContextMenu(createBranchContextMenu());
                    }
                }
            };

    private void showBranches() {
        refs.setCellFactory(refsCellFactory);
        TreeItem<String> localBranches = Utils.createTreeItemWithIcon(Repo.Branches(), localIcon, 16, true);
        TreeItem<String> remotes = Utils.createTreeItemWithIcon(Repo.Remotes(), remoteIcon, 16, true);
        TreeItem<String> tags = Utils.createTreeItemWithIcon(Repo.Tags(), tagsIcon, 16, true);
        tags.getChildren().addAll(repoService.tags(tagIcon));
        localBranches.getChildren().addAll(repoService.localBranches(branchIcon, folderIcon));
        remotes.getChildren().addAll(repoService.remoteBranches(branchIcon, folderIcon));
        // expanding root folders in remotes (e.g. origin) and changing icon
        remotes.getChildren().forEach(item -> {
            item.setExpanded(true);
            item.setGraphic(Utils.createImageView(singleRemoteIcon, 15));
        });
        TreeItem<String> refs = new TreeItem<>(Repo.Refs());
        refs.setExpanded(true);
        refs.getChildren().add(localBranches);
        refs.getChildren().add(remotes);
        refs.getChildren().add(tags);
        this.refs.setRoot(refs);
    }

    private void showFileStatuses() {
        setFileHandler(workingCopyFiles);
        workingCopyFiles.setItems(repoService.modifiedFiles(editedIcon, untrackedIcon));
        setFileHandler(indexFiles);
        indexFiles.setItems(repoService.changedFiles(editedIcon, null, null));
    }

    private void setFileHandler(TableView<File> tableView) {
        tableView.setRowFactory(view -> {
            TableRow<File> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    CompletableFuture.supplyAsync(() -> repoService.workingCopyDiff(row.getItem().getFilePath()))
                            .thenAccept(html -> Platform.runLater(() -> diffView.getEngine().loadContent(html)));
                }
            });
            row.contextMenuProperty().bind(Bindings
                    .when(row.emptyProperty())
                    .then(NO_CONTEXT_MENU)
                    .otherwise(createFileContextMenu()));
            return row;
        });
    }

    private static final ContextMenu NO_CONTEXT_MENU = null;
    private static final String[] FILE_CONTEXT_MENU_ITEMS = {
            "Stage",
            "Discard",
            "Remove",
            "Show in explorer"
    };

    private ContextMenu createFileContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(Arrays.stream(FILE_CONTEXT_MENU_ITEMS)
                .map(MenuItem::new)
                .collect(Collectors.toList()));
        return contextMenu;
    }

    private static final String[] BRANCH_CONTEXT_MENU_ITEMS = {
            "Checkout",
            "Merge",
            "Rebase",
            "Pull",
            "Delete"
    };

    private ContextMenu createBranchContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(Arrays.stream(BRANCH_CONTEXT_MENU_ITEMS)
                .map(MenuItem::new)
                .collect(Collectors.toList()));
        return contextMenu;
    }

    private Image getImage(String path) {
        return new Image(getClass().getClassLoader().getResourceAsStream(path));
    }
}
