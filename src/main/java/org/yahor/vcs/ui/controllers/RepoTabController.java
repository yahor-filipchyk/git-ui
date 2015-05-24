package org.yahor.vcs.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

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
                            List<TreeItem> parents = new LinkedList<>();
                            while (parent != null &&
                                    (!Repo.Branches().equals(parent.getValue()))) {
                                currentBranch = parent.getValue() + "/" + currentBranch;
                                parents.add(parent);
                                parent = parent.getParent();
                            }
                            if (currentBranch.equals(repoService.currentBranch())) {
                                getStyleClass().add("active-branch");
                            }
                        } else {
                            getStyleClass().remove("active-branch");
                        }
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
        setFileDiffHandler(workingCopyFiles);
        workingCopyFiles.setItems(repoService.modifiedFiles(editedIcon, untrackedIcon));
        setFileDiffHandler(indexFiles);
        indexFiles.setItems(repoService.changedFiles(editedIcon, null, null));
    }

    private void setFileDiffHandler(TableView<File> tableView) {
        tableView.setRowFactory(view -> {
            TableRow<File> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    CompletableFuture.supplyAsync(() -> repoService.workingCopyDiff(row.getItem().getFilePath()))
                            .thenAccept(html -> Platform.runLater(() -> diffView.getEngine().loadContent(html)));
                }
            });
            return row;
        });
    }

    private Image getImage(String path) {
        return new Image(getClass().getClassLoader().getResourceAsStream(path));
    }
}
