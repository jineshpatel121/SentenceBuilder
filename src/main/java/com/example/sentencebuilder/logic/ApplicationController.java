package com.example.sentencebuilder.logic;

/**
 * Controller between the UI and business logic (Low Coupling from JavaFX/DB).
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting September 18, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public class ApplicationController {

    private final Vocabulary vocabulary;
    private final SentenceGenerator sentenceGenerator;

    /**
     * Creates the controller with default business-logic collaborators.
     */
    public ApplicationController() {
        this.vocabulary = new Vocabulary();
        this.sentenceGenerator = new SentenceGenerator();
    }

    /**
     * Learns words and counts from one line of imported text.
     *
     * @param line single line from a text file
     */
    public void learnFromLine(String line) {
        vocabulary.learnFromLine(line);
    }

    /**
     * Returns how many distinct words have been learned.
     *
     * @return vocabulary size
     */
    public int getKnownWordCount() {
        return vocabulary.getWordCount();
    }

    /**
     * Starts sentence generation for the UI layer.
     *
     * @param startWordText word selected by the user
     * @return generated sentence (placeholder implementation for now)
     */
    public Sentence generateSentence(String startWordText) {
        return sentenceGenerator.generateFromStartWord(vocabulary, startWordText);
    }
}
