package org.yahor.vcs.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author yfilipchyk
 */
public class RepoTabController implements Initializable {

    @FXML
    private TreeView<String> refs;
    private Image tagIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/tag.png"));
    private Image branchIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/branch.png"));
    private Image remoteIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/remote.png"));
    private Image localIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/local.png"));
    private Image tagsIcon = new Image(getClass().getClassLoader().getResourceAsStream("images/tags.png"));

    public void loadRepo(Repository repository) throws GitAPIException {
        System.out.println(repository.getAllRefs());
        TreeItem<String> localBranches = createTreeItemWithIcon("Branches", localIcon, 16);
        TreeItem<String> remotes = createTreeItemWithIcon("Remotes", remoteIcon, 16);
        TreeItem<String> tags = createTreeItemWithIcon("Tags", tagsIcon, 16);
        localBranches.setExpanded(true);
        remotes.setExpanded(true);
        tags.setExpanded(true);
        repository.getAllRefs().entrySet().stream()
                .map(Map.Entry::getKey)
                .forEach(refName -> {
                    if (refName.startsWith(Constants.R_TAGS)) {
                        addRefToTree(tags, refName, tagIcon);
                    } else if (refName.startsWith(Constants.R_HEADS)) {
                        addRefToTree(localBranches, refName, branchIcon);
                    } else if (refName.startsWith(Constants.R_REMOTES)) {
                        addRefToTree(remotes, refName, branchIcon);
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

    private static void addRefToTree(TreeItem<String> tree, String refName, Image icon) {
        tree.getChildren().add(createTreeItemWithIcon(Repository.shortenRefName(refName), icon, 10));
    }

    private static TreeItem<String> createTreeItemWithIcon(String label, Image icon, int imageHeight) {
        TreeItem<String> treeItem = new TreeItem<>(label);
        ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(imageHeight);
        treeItem.setGraphic(imageView);
        return treeItem;
    }
}
