package org.yahor.vcs.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.eclipse.jgit.lib.Repository;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.git.Tree;
import org.yahor.vcs.ui.model.File;
import org.yahor.vcs.ui.utils.FXUtils;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Repo repo;

    private final Image tagIcon = getImage("images/tag.png");
    private final Image branchIcon = getImage("images/branch.png");
    private final Image remoteIcon = getImage("images/remote.png");
    private final Image singleRemoteIcon = getImage("images/server.png");
    private final Image localIcon = getImage("images/local.png");
    private final Image tagsIcon = getImage("images/tags.png");
    private final Image folderIcon = getImage("images/folder.png");

    private final Image editedIcon = getImage("images/edited.png");
    private final Image untrackedIcon = getImage("images/untracked.png");

    public void loadRepo(Repo repository) {
        this.repo = repository;
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
            item.setGraphic(createImageView(singleRemoteIcon, 15));
        });
        TreeItem<String> refs = new TreeItem<>(Repo.Refs());
        refs.setExpanded(true);
        refs.getChildren().add(localBranches);
        refs.getChildren().add(remotes);
        refs.getChildren().add(tags);
        this.refs.setRoot(refs);
    }

    private void showFileStatuses() {
        // working copy files
        List<File> modified = pathsToFiles(repo.modifiedFiles(false), editedIcon, 15);
        List<File> untracked = pathsToFiles(repo.untrackedFiles(false), untrackedIcon, 15);
        ObservableList<File> modifiedFiles = FXCollections.observableArrayList(concatLists(modified, untracked));
        System.out.println(modifiedFiles);
        workingCopyFiles.setItems(modifiedFiles);

        // index files
        List<File> changed = pathsToFiles(repo.changedFiles(false), editedIcon, 15);
        List<File> newAdded = pathsToFiles(repo.addedFiles(false), null, 15);
        List<File> conflicting = pathsToFiles(repo.conflictingFiles(false), null, 15);
        ObservableList<File> changedFiles = FXCollections.observableArrayList(concatLists(changed, newAdded, conflicting));
        System.out.println(changedFiles);
        indexFiles.setItems(changedFiles);
    }

    private List<File> pathsToFiles(Collection<String> paths, Image icon, int imageHeight) {
        return paths.stream()
                .map(file -> new File(icon != null ? createImageView(icon, imageHeight) : null, file))
                .collect(Collectors.toList());
    }

    private Image getImage(String path) {
        return new Image(getClass().getClassLoader().getResourceAsStream(path));
    }

    private ImageView createImageView(Image icon, int height) {
        ImageView imageView = new ImageView(icon);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(height);
        return imageView;
    }

    @SafeVarargs
    private static <T> List<T> concatLists(List<T> list1, List<T> list2, List<T> ... otherLists) {
        requireNonNull(list1);
        requireNonNull(list2);
        Stream<T> combining = Stream.concat(list1.stream(), list2.stream());
        for (List<T> list : otherLists) {
            combining = Stream.concat(combining, list.stream());
        }
        return combining.collect(Collectors.toList());
    }
}
