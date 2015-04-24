package org.yahor.vcs.ui.git

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.Constants
import scala.collection.JavaConversions._
import scala.collection.mutable

class Repo(val repo: Repository) {

  import Repo._

  def tags: java.util.List[String] = repo.getTags.entrySet().map(_.getKey).toList

  def branches: java.util.Map[String, Object] = {
    var heads: Map[String, Object] = Map.empty
    var remotes: Map[String, Object] = Map.empty
    for ((key, value) <- repo.getAllRefs) {
      if (key.startsWith(Constants.R_HEADS)) {
        if (key.contains('/')) {
          val prefix = key.substring(0, key.indexOf('/'))
          val suffix = key.substring(prefix.length)
          if (heads.contains(prefix)) heads(prefix).asInstanceOf[List] ++
        }
      }
    }
    Map(BRANCHES -> heads, REMOTES -> remotes)
  }

  def getRefs

  def getPath(ref: String): List[String] = ref.split('/').toList

  case class Tree(label: String, var children: List[Tree]) {
    def addChild(tree: Tree) = children = tree :: children
    def add(label: String): None = {
      if (!label.contains('/')) addChild(Tree(label, List()))
      else {
        val prefix = label.substring(0, label.indexOf('/'))
        val suffix = label.substring(prefix.length + 1)
        val matched = children.find(_.label == prefix)
        if (matched.isEmpty) {
          val newItem = Tree(prefix, List())
          newItem.add(suffix)
          addChild(newItem)
        } else {
          matched.get.add(suffix)
        }
      }
    }
  }
}

object Repo {
  val BRANCHES = "Branches"
  val REMOTES = "Remotes"
}