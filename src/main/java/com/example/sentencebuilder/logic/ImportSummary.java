package com.example.sentencebuilder.logic;

import java.nio.file.Path;

/**
 * Immutable result returned after a text file has been processed.
 * Token and distinct-word counts use the project's current import/tokenization rules.
 */
public record ImportSummary(
        Path sourceFile,
        long lineCount,
        long tokenCount,
        int distinctWordCount,
        long fileSizeBytes,
        long elapsedMilliseconds) {
}
