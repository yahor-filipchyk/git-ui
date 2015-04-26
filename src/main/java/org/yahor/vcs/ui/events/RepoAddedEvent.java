package org.yahor.vcs.ui.events;

import java.io.File;

/**
 * @author yfilipchyk
 */
public class RepoAddedEvent implements RepoEvent {

    private File repoDir;
    private String repoName;

    public RepoAddedEvent(File repoDir, String repoName) {
        this.repoDir = repoDir;
        this.repoName = repoName;
    }

    public File getRepoDir() {
        return repoDir;
    }

    public String getRepoName() {
        return repoName;
    }
}
