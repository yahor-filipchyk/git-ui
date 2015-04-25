package org.yahor.vcs.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.git.Tree;
import org.yahor.vcs.ui.utils.FXUtils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author yfilipchyk
 */
public class RepoTabController implements Initializable {

    @FXML
    private TreeView<String> refs;

    private Repo repo;

    private final Image tagIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/tag.png"));
    private final Image branchIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/branch.png"));
    private final Image remoteIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/remote.png"));
    private final Image singleRemoteIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/server.png"));
    private final Image localIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/local.png"));
    private final Image tagsIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/tags.png"));
    private final Image folderIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/folder.png"));

    public void loadRepo(Repo repository) {
        this.repo = repository;
        showBranches();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void showBranches() {
        TreeItem<String> localBranches = FXUtils.createTreeItemWithIcon(Repo.Branches(), localIcon, 16, true);
        TreeItem<String> remotes = FXUtils.createTreeItemWithIcon(Repo.Remotes(), remoteIcon, 16, true);
        TreeItem<String> tags = FXUtils.createTreeItemWithIcon(Repo.Tags(), tagsIcon, 16, true);
        repo.tags().forEach(tag ->
                tags.getChildren()
                        .add(FXUtils.createTreeItemWithIcon(Repository.shortenRefName(tag), tagIcon, 10, false)));
        Map<String, Tree> branches = repo.branches();
        Repo.addFoldersAndBranches(localBranches, branches.get(Repo.Branches()), folderIcon, branchIcon);
        Repo.addFoldersAndBranches(remotes, branches.get(Repo.Remotes()), folderIcon, branchIcon);
        // expanding root folders in remotes (e.g. origin) and changing icon
        remotes.getChildren().forEach(item -> {
            item.setExpanded(true);
            ImageView remoteServerIcon = new ImageView(singleRemoteIcon);
            remoteServerIcon.setFitHeight(15);
            item.setGraphic(remoteServerIcon);
        });
        TreeItem<String> refs = new TreeItem<>(Repo.Refs());
        refs.setExpanded(true);
        refs.getChildren().add(localBranches);
        refs.getChildren().add(remotes);
        refs.getChildren().add(tags);
        this.refs.setRoot(refs);
    }
}
