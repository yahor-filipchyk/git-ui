package org.yahor.vcs.ui.git

/**
 * @author yahor-filipchyk
 */
case class Tree(label: String, var children: List[Tree]) {
  def addChild(tree: Tree) = children = (tree :: children).sortBy(_.label)
    .sortWith((t1, t2) => t1.children.isEmpty && t2.children.nonEmpty)

  def add(label: String) {
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
