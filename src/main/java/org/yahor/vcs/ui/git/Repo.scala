package org.yahor.vcs.ui.git

import java.io.File
import javafx.scene.control.TreeItem
import javafx.scene.image.Image

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{Constants, Repository}
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.yahor.vcs.ui.utils.FXUtils

import scala.collection.JavaConversions._

class Repo(val repo: Repository) {

  import Repo._

  def close(): Unit = repo.close()

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

  def createRepo(dir: File): Repo = {
    val git = Git.init()
      .setDirectory(dir)
      .call()
    new Repo(git.getRepository)
  }

  def openRepo(dir: File): Repo = {
    val builder = new FileRepositoryBuilder
    val repository = builder.setGitDir(dir)
      .readEnvironment()
      .findGitDir()
      .build()
    new Repo(repository)
  }

  def cloneRepo(url: String, dir: File): Repo = {
    val newFolder = new File(dir, repoName(url))
    newFolder.mkdir()
    val git = Git.cloneRepository()
      .setURI(url)
      .setDirectory(newFolder)
      .call()
    new Repo(git.getRepository)
  }

  def repoName(url: String): String = {
    if (!url.contains("/"))
      url
    else {
      val name = url.substring(url.lastIndexOf('/') + 1)
      if (name endsWith ".git") name.substring(0, name.length - ".git".length)
      else name
    }
  }

  def addFoldersAndBranches(rootTreeItem: TreeItem[String], tree: Tree, folderIcon: Image, branchIcon: Image): Unit =
    tree match {
      case Tree("", children) => children.foreach(t => addFoldersAndBranches(rootTreeItem, t, folderIcon, branchIcon))
      case Tree(label, List()) => {
        rootTreeItem.getChildren.add(FXUtils.createTreeItemWithIcon(label, branchIcon, 10, false))
      }
      case Tree(label, children) if children.nonEmpty => {
        val folderItem = FXUtils.createTreeItemWithIcon(label, folderIcon, 15, false)
        children.foreach(t => addFoldersAndBranches(folderItem, t, folderIcon, branchIcon))
        rootTreeItem.getChildren.add(folderItem)
      }
    }

}