package com.example.sentencebuilder.logic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Validates a user-selected import file before the Import button is enabled.
 */
public final class ImportFileValidator {
    private ImportFileValidator() {
    }

    /**
     * Checks whether a path points to a readable plain-text file.
     *
     * @param path path selected by the user
     * @return an empty string when valid, otherwise a user-facing error message
     */
    public static String validate(Path path) {
        if (path == null) {
            return "Choose a text file before importing.";
        }
        if (!Files.exists(path)) {
            return "The selected file does not exist.";
        }
        if (!Files.isRegularFile(path)) {
            return "The selected path is not a file.";
        }
        if (!Files.isReadable(path)) {
            return "The selected file cannot be read.";
        }

        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".txt")) {
            return "Choose a plain-text (.txt) file.";
        }
        return "";
    }
}
