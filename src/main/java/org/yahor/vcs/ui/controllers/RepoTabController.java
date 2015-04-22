package org.yahor.vcs.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yfilipchyk
 */
public class RepoTabController implements Initializable {

    @FXML
    private TreeView<String> refs;

    public void loadRepo(Repository repository) throws GitAPIException {
        System.out.println(repository.getAllRefs());
        TreeItem<String> localBranches = new TreeItem<>("Branches");
        TreeItem<String> remotes = new TreeItem<>("Remotes");
        TreeItem<String> tags = new TreeItem<>("Tags");
        localBranches.setExpanded(true);
        remotes.setExpanded(true);
        tags.setExpanded(true);
        repository.getAllRefs().entrySet().stream()
                .map(Map.Entry::getKey)
                .forEach(refName -> {
                    if (refName.startsWith(Constants.R_TAGS)) {
                        addRefToTree(tags, refName);
                    } else if (refName.startsWith(Constants.R_HEADS)) {
                        addRefToTree(localBranches, refName);
                    } else if (refName.startsWith(Constants.R_REMOTES)) {
                        addRefToTree(remotes, refName);
                    }
                });
        TreeItem<String> refs = new TreeItem<>("Refs");
        refs.setExpanded(true);
        refs.getChildren().add(localBranches);
        refs.getChildren().add(remotes);
        refs.getChildren().add(tags);
        this.refs.setRoot(refs);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private static void addRefToTree(TreeItem<String> tree, String refName) {
        tree.getChildren().add(new TreeItem<>(Repository.shortenRefName(refName)));
    }
}
