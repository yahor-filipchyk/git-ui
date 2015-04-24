package org.yahor.vcs.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
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

    private Image tagIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/tag.png"));
    private Image branchIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/branch.png"));
    private Image remoteIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/remote.png"));
    private Image localIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/local.png"));
    private Image tagsIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/tags.png"));
    private Image folderIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/folder.png"));

    public void loadRepo(Repo repository) throws GitAPIException {
        this.repo = repository;
        showBranches();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void showBranches() {
        TreeItem<String> localBranches = FXUtils.createTreeItemWithIcon("Branches", localIcon, 16);
        TreeItem<String> remotes = FXUtils.createTreeItemWithIcon("Remotes", remoteIcon, 16);
        TreeItem<String> tags = FXUtils.createTreeItemWithIcon("Tags", tagsIcon, 16);
        localBranches.setExpanded(true);
        remotes.setExpanded(true);
        tags.setExpanded(true);
        repo.tags().forEach(tag -> addRefToTree(tags, tag, tagIcon));
        Map<String, Tree> branches = repo.branches();
        Repo.addFoldersAndBranches(localBranches, branches.get(Repo.BRANCHES()), folderIcon, branchIcon);
        Repo.addFoldersAndBranches(remotes, branches.get(Repo.REMOTES()), folderIcon, branchIcon);
        remotes.getChildren().forEach(item -> item.setExpanded(true));
        TreeItem<String> refs = new TreeItem<>("Refs");
        refs.setExpanded(true);
        refs.getChildren().add(localBranches);
        refs.getChildren().add(remotes);
        refs.getChildren().add(tags);
        this.refs.setRoot(refs);
    }

    private static void addRefToTree(TreeItem<String> tree, String refName, Image icon) {
        tree.getChildren().add(FXUtils.createTreeItemWithIcon(Repository.shortenRefName(refName), icon, 10));
    }
}
