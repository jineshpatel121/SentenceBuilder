package com.example.sentencebuilder.logic;

import java.nio.file.Path;

/**
 * Immutable result returned after a text file has been read for import.
 * The token count is intentionally separate from the project's final definition
 * of a word so the team can replace the tokenization policy later.
 */
public record ImportSummary(
        Path sourceFile,
        long lineCount,
        long tokenCount,
        long fileSizeBytes,
        long elapsedMilliseconds) {
}
