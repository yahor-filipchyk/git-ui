package org.yahor.vcs.ui.events

import java.io.File

/**
 * @author yahor-filipchyk
 */
sealed abstract class RepoEvent(repoDir: File)

case class RepoAddedEvent(repoDir: File,
                          repoName: String) extends RepoEvent(repoDir)

case class RepoCreatedEvent(repoDir: File,
                            repoName: String) extends RepoEvent(repoDir)

case class RepoClonedEvent(destinationDir: File,
                           url: String,
                           repoName: String) extends RepoEvent(destinationDir)

case class RepoStashedEvent(repoDir: File,
                            message: String,
                            keepStaged: Boolean = false) extends RepoEvent(repoDir)

case class RepoPulledEvent(repoDir: File,
                           remote: String,
                           branch: String,
                           commit: Boolean = true,
                           rebase: Boolean = false) extends RepoEvent(repoDir)

case class CommitCheckedOutEvent(repoDir: File,
                                 commit: String,
                                 discardChanges: Boolean = false) extends RepoEvent(repoDir)

case class BranchCheckedOutEvent(repoDir: File,
                                 remoteBranch: String,
                                 localBranch: String,
                                 track: Boolean = true) extends RepoEvent(repoDir)

case class RepoTaggedEvent(repoDir: File,
                           tagName: String,
                           commit: String = "",
                           push: Boolean = false,
                           message: String = "") extends RepoEvent(repoDir)

case class TagRemovedEvent(repoDir: File,
                           tagName: String,
                           removeFromAllRemotes: Boolean = false) extends RepoEvent(repoDir)

case class RepoFetchedEvent(repoDir: File,
                            allRemotes: Boolean = true,
                            prune: Boolean = false,
                            fetchTags: Boolean = false) extends RepoEvent(repoDir)

case class BranchCreatedEvent(repoDir: File,
                              branchName: String,
                              commit: String = "",
                              checkout: Boolean = true) extends RepoEvent(repoDir)

case class BranchesDeletedEvent(repoDir: File,
                                branches: java.util.List[String],
                                forceDelete: Boolean = false) extends RepoEvent(repoDir)

case class BranchMergedEvent(repoDir: File,
                             branch: String,
                             commit: Boolean = true,
                             rebase: Boolean = false) extends RepoEvent(repoDir)

case class RepoPushedEvent(repoDir: File,
                           remote: String,
                           branches: java.util.List[String],
                           pushTags: Boolean = false) extends RepoEvent(repoDir)

case class RepoCommittedEvent(repoDir: File,
                              message: String,
                              amend: Boolean = false,
                              push: Boolean = false) extends RepoEvent(repoDir)

case class ChangesDiscarded(repoDir: File,
                            files: java.util.List[String]) extends RepoEvent(repoDir)