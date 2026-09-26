package com.example.sentencebuilder.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
import java.util.concurrent.CancellationException;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

/**
 * Reads a text file without changing application or database state.
 * This gives the JavaFX layer measurable work that can run on a background
 * thread while the final database import pipeline is developed separately.
 */
public class TextFileAnalyzer {

    /**
     * Reads a text file, counts lines and whitespace-delimited tokens, and reports progress.
     * The token count is a neutral import metric; it is not the project's final definition of a word.
     *
     * @param source file to read using UTF-8
     * @param cancelled callback used to stop work when the user cancels
     * @param progress callback receiving completed lines and total lines
     * @return immutable summary of the completed read
     * @throws IOException if the file cannot be read
     * @throws CancellationException if cancellation is requested
     */
    public ImportSummary analyze(
            Path source,
            BooleanSupplier cancelled,
            BiConsumer<Long, Long> progress) throws IOException {

        long startNanos = System.nanoTime();
        long totalLines = countLines(source, cancelled);
        long completedLines = 0;
        long tokenCount = 0;

        progress.accept(0L, totalLines);
        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                checkCancelled(cancelled);
                tokenCount += countTokens(line);
                completedLines++;
                progress.accept(completedLines, totalLines);
            }
        }

        long elapsedMilliseconds = (System.nanoTime() - startNanos) / 1_000_000L;
        return new ImportSummary(
                source,
                completedLines,
                tokenCount,
                Files.size(source),
                elapsedMilliseconds);
    }

    /**
     * Counts lines in a first pass so the second pass can report determinate progress.
     *
     * @param source file being analyzed
     * @param cancelled cancellation callback
     * @return number of lines in the file
     * @throws IOException if the file cannot be read
     */
    private long countLines(Path source, BooleanSupplier cancelled) throws IOException {
        long lineCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            while (reader.readLine() != null) {
                checkCancelled(cancelled);
                lineCount++;
            }
        }
        return lineCount;
    }

    /**
     * Counts whitespace-delimited tokens for a progress/import summary only.
     *
     * @param line one line of text from the source file
     * @return number of tokens found on the line
     */
    private long countTokens(String line) {
        return new StringTokenizer(line).countTokens();
    }

    /**
     * Ends analysis promptly when the JavaFX task has been cancelled.
     *
     * @param cancelled callback that reports cancellation state
     */
    private void checkCancelled(BooleanSupplier cancelled) {
        if (cancelled.getAsBoolean()) {
            throw new CancellationException("Text file analysis was cancelled.");
        }
    }
}
