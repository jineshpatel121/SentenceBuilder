package com.example.sentencebuilder.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the side-effect-free text-file analysis used by the JavaFX worker task.
 */
class TextFileAnalyzerTest {

    @TempDir
    Path tempDirectory;

    /**
     * Counts lines and neutral whitespace-delimited tokens without changing external state.
     */
    @Test
    void analyzesTextFile() throws IOException {
        Path file = tempDirectory.resolve("sample.txt");
        Files.writeString(file, "one two three\nfour five\nsix");

        AtomicLong completedLines = new AtomicLong();
        AtomicLong totalLines = new AtomicLong();

        ImportSummary summary = new TextFileAnalyzer().analyze(
                file,
                () -> false,
                (completed, total) -> {
                    completedLines.set(completed);
                    totalLines.set(total);
                });

        assertEquals(3, summary.lineCount());
        assertEquals(6, summary.tokenCount());
        assertEquals(3, completedLines.get());
        assertEquals(3, totalLines.get());
        assertTrue(summary.fileSizeBytes() > 0);
    }

    /**
     * Cancellation is honored so long-running reads can stop without freezing the UI.
     */
    @Test
    void stopsWhenCancelled() throws IOException {
        Path file = tempDirectory.resolve("sample.txt");
        Files.writeString(file, "one two three");

        assertThrows(
                CancellationException.class,
                () -> new TextFileAnalyzer().analyze(
                        file,
                        () -> true,
                        (completed, total) -> {
                        }));
    }
}
