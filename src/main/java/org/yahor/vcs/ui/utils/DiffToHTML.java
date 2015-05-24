package org.yahor.vcs.ui.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yahor-filipchyk
 */
public class DiffToHTML {

    private static String template;
    private static String diffLine;
    private final static String HUNK_STARTING_TAG = "<div class=\"hunk\">\n";
    private final static String HUNK_CLOSING_TAG = "\n</div>\n";
    private final static Pattern CHANGES = Pattern.compile("-(\\d+),(\\d+) \\+(\\d+),(\\d)");
    private final static Pattern NEW_LINE = Pattern.compile("\n");

    static {
        try {
            Path templatePath = Paths.get(DiffToHTML.class.getResource("/diff/diff_template.html").toURI());
            Path diffLinePath = Paths.get(DiffToHTML.class.getResource("/diff/diff_line.html").toURI());
            template = Utils.getFileContents(templatePath);
            diffLine = Utils.getFileContents(diffLinePath);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convert(String fileName, String diff) {
        String[] splitted = diff.split("\\s@@\\s");
        StringBuilder chunks = new StringBuilder();
        AtomicInteger totalAdded = new AtomicInteger(0);
        AtomicInteger totalRemoved = new AtomicInteger(0);
        for (int i = 1; i < splitted.length; i += 2) {
            String chunkInfo = splitted[i].trim();
            Matcher matcher = CHANGES.matcher(chunkInfo);
            Preconditions.checkState(matcher.find());
            int oldStartingPosition = Integer.parseInt(matcher.group(1));
            int newStartingPosition = Integer.parseInt(matcher.group(3));
            AtomicInteger oldCurrent = new AtomicInteger(oldStartingPosition);
            AtomicInteger newCurrent = new AtomicInteger(newStartingPosition);
            Stream<String> lines = NEW_LINE.splitAsStream(splitted[i + 1]).map(line -> {
                if (line.startsWith("-")) {
                    totalRemoved.incrementAndGet();
                    return String.format(diffLine, "removed", oldCurrent.getAndIncrement(), "",
                            StringEscapeUtils.escapeHtml4(line));
                } else if (line.startsWith("+")) {
                    totalAdded.incrementAndGet();
                    return String.format(diffLine, "added", "", newCurrent.getAndIncrement(),
                            StringEscapeUtils.escapeHtml4(line));
                } else {
                    return String.format(diffLine, "none", oldCurrent.getAndIncrement(), newCurrent.getAndIncrement(),
                            StringEscapeUtils.escapeHtml4(line));
                }
            });
            chunks.append(HUNK_STARTING_TAG)
                    .append(lines.collect(Collectors.joining("\n")))
                    .append(HUNK_CLOSING_TAG);
        }
        return template.replace("{added}", String.valueOf(totalAdded.get()))
                .replace("{removed}", String.valueOf(totalRemoved.get()))
                .replace("{header}", fileName)
                .replace("{hunks}", chunks.toString());
    }
}
