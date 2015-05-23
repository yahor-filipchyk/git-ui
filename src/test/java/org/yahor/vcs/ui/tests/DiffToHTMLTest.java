package org.yahor.vcs.ui.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yahor.vcs.ui.git.Repo;

import java.nio.file.Paths;

/**
 * @author yahor-filipchyk
 */
public class DiffToHTMLTest {

    private static Repo repo;

    @Test
    public void testDiffToHTML() {

    }

    @BeforeClass
    public static void setup() {
        repo = Repo.openRepo(Paths.get(System.getProperty("user.dir"), ".git").toFile());
    }

    @AfterClass
    public static void tearDown() {
        repo.close();
    }
}
