package com.example.sentencebuilder.logic;

/**
 * Names the sentence-generation algorithms supported by the logic layer.
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting October 10, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public enum GenerationMethod {

    /**
     * Chooses each next word in proportion to follower occurrence counts.
     */
    WEIGHTED_RANDOM,

    /**
     * Always chooses the follower with the highest occurrence count.
     */
    MOST_COMMON
}
