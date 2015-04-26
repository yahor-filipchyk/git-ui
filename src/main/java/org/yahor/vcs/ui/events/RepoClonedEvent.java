package org.yahor.vcs.ui.events;

import java.io.File;

/**
 * @author yahor-filipchyk
 */
public class RepoClonedEvent implements RepoEvent {

    private String url;
    private File destinationDir;
    private String repoName;

    public RepoClonedEvent(String url, File destinationDir, String repoName) {
        this.url = url;
        this.destinationDir = destinationDir;
        this.repoName = repoName;
    }

    public String getUrl() {
        return url;
    }

    public File getDestinationDir() {
        return destinationDir;
    }

    public String getRepoName() {
        return repoName;
    }
}
