package com.example.sentencebuilder.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the user-input rules applied before a file import can start.
 */
class ImportFileValidatorTest {

    @TempDir
    Path tempDirectory;

    /**
     * A readable .txt file should pass validation.
     */
    @Test
    void acceptsReadableTextFile() throws IOException {
        Path file = tempDirectory.resolve("sample.txt");
        Files.writeString(file, "sample text");

        assertEquals("", ImportFileValidator.validate(file));
    }

    /**
     * Non-text input should be rejected before background work begins.
     */
    @Test
    void rejectsNonTextFile() throws IOException {
        Path file = tempDirectory.resolve("sample.pdf");
        Files.writeString(file, "not actually a PDF, but the extension is invalid");

        assertTrue(ImportFileValidator.validate(file).contains(".txt"));
    }

    /**
     * A missing path should produce a clear validation message.
     */
    @Test
    void rejectsMissingFile() {
        Path file = tempDirectory.resolve("missing.txt");

        assertEquals(
                "The selected file does not exist.",
                ImportFileValidator.validate(file));
    }
}
