package org.yahor.vcs.ui.services

import java.io.File
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.TreeItem
import javafx.scene.image.Image

import org.eclipse.jgit.lib.Repository
import org.yahor.vcs.ui.git.Repo
import org.yahor.vcs.ui.model.{File => FileStatus, Tree}
import org.yahor.vcs.ui.utils.Utils._
import org.yahor.vcs.ui.utils.{DiffToHTML, Utils}

import scala.collection.JavaConversions._

/**
 * @author yahor-filipchyk
 */
class RepoService(val repo: Repo, val name: String) {

  import RepoService._

  def close(): Unit = repo.close()

  def currentBranch: String = repo.currentBranch

  def repoDir: File = repo.repo.getDirectory

  def tags(icon: Image): java.util.List[TreeItem[String]] =
    repo.tags.map(tag => createTreeItemWithIcon(Repository.shortenRefName(tag), icon, 10, false))

  def localBranches(branchIcon: Image, folderIcon: Image): java.util.List[TreeItem[String]] =
    addFoldersAndBranches(new TreeItem[String](""),
      repo.branches(Repo.Branches),
      folderIcon,
      branchIcon).getChildren

  def remoteBranches(branchIcon: Image, folderIcon: Image): java.util.List[TreeItem[String]] =
    addFoldersAndBranches(new TreeItem[String](""),
      repo.branches(Repo.Remotes),
      folderIcon,
      branchIcon).getChildren

  def changedFiles(editedIcon: Image, addedFilesIcon: Image, conflictingIcon: Image): ObservableList[FileStatus] = {
    val changed: List[FileStatus] = pathsToFiles(repo.changedFiles(refresh = false), editedIcon, 15)
    val newAdded: List[FileStatus] = pathsToFiles(repo.addedFiles(refresh = false), addedFilesIcon, 15)
    val conflicting: List[FileStatus] = pathsToFiles(repo.conflictingFiles(refresh = false), conflictingIcon, 15)
    FXCollections.observableArrayList(concatLists(changed, newAdded, conflicting))
  }

  def modifiedFiles(editedIcon: Image, untrackedIcon: Image): ObservableList[FileStatus] = {
    val modified: List[FileStatus] = pathsToFiles(repo.modifiedFiles(refresh = false), editedIcon, 15)
    val untracked: List[FileStatus] = pathsToFiles(repo.untrackedFiles(refresh = false), untrackedIcon, 15)
    FXCollections.observableArrayList(concatLists(modified, untracked))
  }

  def workingCopyDiff(file: String): String = DiffToHTML.convert(file, repo.diffAgainstLatest(file))

  def checkoutBranch(remoteBranch: String, localBranch: String, track: Boolean) =
    repo.checkoutBranch(remoteBranch, localBranch, track)
}

object RepoService {

  def createRepo(path: File, name: String) = new RepoService(Repo.createRepo(path), name)

  def cloneRepo(url: String, dir: File, name: String) = new RepoService(Repo.cloneRepo(url, dir), name)

  def openRepo(path: File, name: String) = new RepoService(Repo.openRepo(path), name)

  def pathsToFiles(paths: Iterable[String], icon: Image, imageHeight: Int): List[FileStatus] =
    paths.map(file => new FileStatus(if (icon != null) createImageView(icon, imageHeight) else null, file)).toList

  def addFoldersAndBranches(rootTreeItem: TreeItem[String], tree: Tree, folderIcon: Image, branchIcon: Image): TreeItem[String] = {
    tree match {
      case Tree("", children) => children.foreach(t => addFoldersAndBranches(rootTreeItem, t, folderIcon, branchIcon))
      case Tree(label, List()) => {
        rootTreeItem.getChildren.add(Utils.createTreeItemWithIcon(label, branchIcon, 10, false))
      }
      case Tree(label, children) if children.nonEmpty => {
        val folderItem = Utils.createTreeItemWithIcon(label, folderIcon, 15, false)
        children.foreach(t => addFoldersAndBranches(folderItem, t, folderIcon, branchIcon))
        rootTreeItem.getChildren.add(folderItem)
      }
    }
    rootTreeItem
  }
}
