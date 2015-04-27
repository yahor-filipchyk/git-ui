package org.yahor.vcs.ui.events

import java.io.File

/**
 * @author yahor-filipchyk
 */
sealed trait RepoEvent

case class RepoAddedEvent(repoDir: File, repoName: String) extends RepoEvent

case class RepoClonedEvent(url: String, destinationDir: File, repoName: String) extends RepoEvent
