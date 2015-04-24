package org.yahor.vcs.ui.git

import javafx.scene.control.TreeItem
import javafx.scene.image.Image

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.Constants
import org.yahor.vcs.ui.utils.FXUtils
import scala.collection.JavaConversions._

class Repo(val repo: Repository) {

  import Repo._

  def tags: java.util.List[String] = repo.getTags.entrySet().map(_.getKey).toList.sorted

  def branches: java.util.Map[String, Tree] = {
    val heads = Tree("", List())
    val remotes = Tree("", List())
    for ((key, value) <- repo.getAllRefs) {
      if (key.startsWith(Constants.R_HEADS)) {
        heads.add(Repository.shortenRefName(key))
      } else if (key.startsWith(Constants.R_REMOTES)) {
        remotes.add(Repository.shortenRefName(key))
      }
    }
    Map(BRANCHES -> heads, REMOTES -> remotes)
  }
}

object Repo {

  val BRANCHES = "Branches"
  val REMOTES = "Remotes"

  def addFoldersAndBranches(rootTreeItem: TreeItem[String], tree: Tree, folderIcon: Image, branchIcon: Image) {
    tree match {
      case Tree("", children) => children.foreach(t => addFoldersAndBranches(rootTreeItem, t, folderIcon, branchIcon))
      case Tree(label, List()) => rootTreeItem.getChildren.add(FXUtils.createTreeItemWithIcon(label, branchIcon, 10))
      case Tree(label, head :: rest) => {
        val folderItem = FXUtils.createTreeItemWithIcon(label, folderIcon, 15)
        (head :: rest).foreach(t => addFoldersAndBranches(folderItem, t, folderIcon, branchIcon))
        rootTreeItem.getChildren.add(folderItem)
      }
    }
  }
}