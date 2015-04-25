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
    Map(Branches -> heads, Remotes -> remotes)
  }
}

object Repo {

  val Refs = "Refs"
  val Branches = "Branches"
  val Remotes = "Remotes"
  val Tags = "Tags"

  def addFoldersAndBranches(rootTreeItem: TreeItem[String], tree: Tree, folderIcon: Image, branchIcon: Image) {
    tree match {
      case Tree("", children) => children.foreach(t => addFoldersAndBranches(rootTreeItem, t, folderIcon, branchIcon))
      case Tree(label, List()) =>
        rootTreeItem.getChildren.add(FXUtils.createTreeItemWithIcon(label, branchIcon, 10, false))
      case Tree(label, children) if children.nonEmpty =>
        val folderItem = FXUtils.createTreeItemWithIcon(label, folderIcon, 15, false)
        children.foreach(t => addFoldersAndBranches(folderItem, t, folderIcon, branchIcon))
        rootTreeItem.getChildren.add(folderItem)
    }
  }
}