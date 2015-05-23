package org.yahor.vcs.ui.git

import java.io.{ByteArrayOutputStream, StringWriter, BufferedOutputStream, File}
import javafx.scene.control.TreeItem
import javafx.scene.image.Image

import org.eclipse.jgit.api.{Status, StatusCommand, Git}
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.{ObjectId, Constants, Repository}
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.{FileTreeIterator, CanonicalTreeParser, AbstractTreeIterator}
import org.yahor.vcs.ui.model.Tree
import org.yahor.vcs.ui.utils.Utils

import scala.collection.JavaConversions._

class Repo(val repo: Repository) {

  import Repo._

  private var status: Status = null

  private def git() = new Git(repo)

  private def statusCmd = git().status()

  private def status(refresh: Boolean): Status = {
    if (refresh || status == null) {
      status = statusCmd.call()
    }
    status
  }

  def close(): Unit = repo.close()

  def tags: List[String] = repo.getTags.entrySet().map(_.getKey).toList.sorted

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

  def createBranch(name: String): Unit = git().branchCreate().setName(name).call()

  def deleteBranch(name: String): Unit = git().branchDelete().setBranchNames(name).call()

  def changedFiles(refresh: Boolean): java.util.Set[String] = status(refresh).getChanged

  def addedFiles(refresh: Boolean): java.util.Set[String] = status(refresh).getAdded

  def modifiedFiles(refresh: Boolean): java.util.Set[String] = status(refresh).getModified

  def untrackedFiles(refresh: Boolean): java.util.Set[String] = status(refresh).getUntracked

  def conflictingFiles(refresh: Boolean): java.util.Set[String] = status(refresh).getConflicting

  def diffAgainstLatest(file: String): String = {
    val outStream = new ByteArrayOutputStream()
    val formatter = new DiffFormatter(outStream)
    formatter.setRepository(repo)
    val commitTreeIterator = prepareTreeParser(repo, Constants.HEAD)
    val workTreeIterator = new FileTreeIterator(repo)
    val diffEntries = formatter.scan(commitTreeIterator, workTreeIterator)
    diffEntries.filter(entry => file == entry.getNewPath).foreach(formatter.format)
    try {
      outStream.toString("utf-8")
    } finally {
      outStream.close()
    }
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

  def prepareTreeParser(repo: Repository, objectId: String): AbstractTreeIterator = {
    val walk = new RevWalk(repo)
    val commit = walk.parseCommit(ObjectId.fromString(objectId))
    val tree = walk.parseTree(commit.getTree.getId)
    val oldTreeParser = new CanonicalTreeParser()
    val oldReader = repo.newObjectReader()
    try {
      oldTreeParser.reset(oldReader, tree.getId)
    } finally {
      oldReader.release()
    }
    walk.dispose()
    oldTreeParser
  }
}