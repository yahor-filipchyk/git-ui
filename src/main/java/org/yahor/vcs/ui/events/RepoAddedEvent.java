package org.yahor.vcs.ui.events;

import java.io.File;

/**
 * @author yfilipchyk
 */
public interface RepoAddedEvent {

    File getRepoDir();
    String getRepoName();
}
