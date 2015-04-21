package org.yahor.vcs.ui.utils;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author yahor-filipchyk
 */
public enum Language {

    ENGLISH("English", new Locale("en")),
    BELARUSIAN("Беларуская", new Locale("by")),
    RUSSIAN("Русский", new Locale("ru"));

    private final String label;
    private final Locale locale;

    Language(String label, Locale locale) {
        this.label = label;
        this.locale = locale;
    }

    public static Locale getByLabelOrDefault(String label) {
        return Arrays.stream(values())
                .filter(language -> language.label.equalsIgnoreCase(label))
                .findFirst()
                .orElse(ENGLISH).locale;
    }
}
