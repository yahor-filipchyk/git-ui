package org.yahor.vcs.ui.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yahor.vcs.ui.git.Repo;
import org.yahor.vcs.ui.utils.DiffToHTML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * @author yahor-filipchyk
 */
public class DiffToHTMLTest {

    private static Repo repo;
    private static final String TEMPLATE = "src/main/resources/diff/diff_template.html";
    private static final Path TEMPLATE_PATH = Paths.get(getUserDir(), TEMPLATE);

    @Test
    public void testDiffToHTML() throws URISyntaxException, IOException {
        String fileContents = getFileContents(TEMPLATE_PATH);
        String edited = fileContents.replace("{header}", "diff_template.html");
        writeToFile(edited, TEMPLATE_PATH.toFile());
        try {
            String diff = repo.diffAgainstLatest(TEMPLATE);
            String html = DiffToHTML.convert(TEMPLATE, diff);
            assertTrue(String.format("Diff html should contain following hunk %s\nActual was %s", REMOVED_LINE, html),
                    html.contains(REMOVED_LINE));
        } finally {
            writeToFile(fileContents, TEMPLATE_PATH.toFile());
        }
    }

    @BeforeClass
    public static void setup() {
        repo = Repo.openRepo(Paths.get(getUserDir(), ".git").toFile());
    }

    @AfterClass
    public static void tearDown() {
        repo.close();
    }

    private static String getFileContents(Path file) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(file), Charset.forName("utf-8"));
    }

    private static void writeToFile(String contents, File file) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
            writer.write(contents);
        } catch (IOException ex) {
            throw ex;
        }
    }

    private static String getUserDir() {
        return System.getProperty("user.dir");
    }

    private static final String REMOVED_LINE = "    <pre class=\"source\">-        " +
            "&lt;span class=&quot;file&quot;&gt;{header}&lt;/span&gt;</pre>";
}
