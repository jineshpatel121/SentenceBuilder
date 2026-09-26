package com.example.sentencebuilder.logic;

import java.util.List;
import java.util.Objects;

/**
 * Information Expert for an ordered list of words in one sentence.
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting September 18, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public class Sentence {

    private final List<Word> words;

    /**
     * Builds a sentence from words in reading order.
     *
     * @param orderedWords words in order; must not be empty
     */
    public Sentence(List<Word> orderedWords) {
        Objects.requireNonNull(orderedWords, "orderedWords");
        if (orderedWords.isEmpty()) {
            throw new IllegalArgumentException("A sentence must contain at least one word.");
        }
        this.words = List.copyOf(orderedWords);
    }

    /**
     * Returns the words in this sentence.
     *
     * @return unmodifiable word list
     */
    public List<Word> getWords() {
        return words;
    }

    /**
     * Joins word texts into one string for display.
     *
     * @return spaced sentence text
     */
    public String asText() {
        StringBuilder builder = new StringBuilder();
        for (int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
            if (wordIndex > 0) {
                builder.append(' ');
            }
            builder.append(words.get(wordIndex).getText());
        }
        return builder.toString();
    }
}
