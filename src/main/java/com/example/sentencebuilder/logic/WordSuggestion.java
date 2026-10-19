package com.example.sentencebuilder.logic;

/**
 * One autocomplete candidate for the next word after a completed token.
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting October 15, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public record WordSuggestion(String wordText, int followerCount) {
}
