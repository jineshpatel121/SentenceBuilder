package com.example.sentencebuilder.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tokenization policy for imported plain-text lines (logic layer only).
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting September 26, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public final class ImportTokenizer {

    private static final String TRAILING_PUNCTUATION = ".,;:!?\"'()";

    private ImportTokenizer() {
    }

    /**
     * Splits a line into word tokens using the project's first-pass import rules.
     * Whitespace separates tokens; leading and trailing punctuation on each token is removed.
     *
     * @param line one line from an imported text file
     * @return normalized tokens in order; empty when the line is blank
     */
    public static List<String> tokenizeLine(String line) {
        Objects.requireNonNull(line, "line");
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return List.of();
        }

        String[] rawTokens = trimmedLine.split("\\s+");
        List<String> tokens = new ArrayList<>(rawTokens.length);
        for (String rawToken : rawTokens) {
            String cleaned = stripEdgePunctuation(rawToken);
            String normalized = Word.normalize(cleaned);
            if (!normalized.isEmpty()) {
                tokens.add(normalized);
            }
        }
        return tokens;
    }

    /**
     * Removes punctuation attached to the start or end of a raw token.
     *
     * @param rawToken token before normalization
     * @return token with edge punctuation removed
     */
    private static String stripEdgePunctuation(String rawToken) {
        int startIndex = 0;
        int endIndex = rawToken.length();
        while (startIndex < endIndex && TRAILING_PUNCTUATION.indexOf(rawToken.charAt(startIndex)) >= 0) {
            startIndex++;
        }
        while (endIndex > startIndex && TRAILING_PUNCTUATION.indexOf(rawToken.charAt(endIndex - 1)) >= 0) {
            endIndex--;
        }
        return rawToken.substring(startIndex, endIndex);
    }
}
