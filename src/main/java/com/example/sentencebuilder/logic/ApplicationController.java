package com.example.sentencebuilder.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller between the UI and business logic (Low Coupling from JavaFX/DB).
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting September 18, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public class ApplicationController {

    private final Vocabulary vocabulary;
    private final SentenceGenerator sentenceGenerator;
    private final List<Sentence> generatedSentences;

    /**
     * Creates the controller with default business-logic collaborators.
     */
    public ApplicationController() {
        this.vocabulary = new Vocabulary();
        this.sentenceGenerator = new SentenceGenerator();
        this.generatedSentences = new ArrayList<>();
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
     * Starts sentence generation for the UI layer using weighted random selection.
     *
     * @param startWordText word selected by the user
     * @return generated sentence
     */
    public Sentence generateSentence(String startWordText) {
        return rememberGenerated(sentenceGenerator.generateFromStartWord(vocabulary, startWordText));
    }

    /**
     * Generates a sentence with a caller-defined maximum length.
     *
     * @param startWordText word selected by the user
     * @param maxWords maximum number of words, including the start word
     * @return generated sentence
     */
    public Sentence generateSentence(String startWordText, int maxWords) {
        return rememberGenerated(
                sentenceGenerator.generateFromStartWord(vocabulary, startWordText, maxWords));
    }

    /**
     * Generates a sentence using an explicit algorithm and length limit.
     *
     * @param startWordText word selected by the user
     * @param maxWords maximum number of words, including the start word
     * @param method generation algorithm to apply
     * @return generated sentence
     */
    public Sentence generateSentence(String startWordText, int maxWords, GenerationMethod method) {
        return rememberGenerated(
                sentenceGenerator.generateFromStartWord(vocabulary, startWordText, maxWords, method));
    }

    /**
     * Returns ranked next-word suggestions after a completed previous word.
     *
     * @param previousWordText the finished word before the cursor
     * @param maxSuggestions maximum number of choices to return
     * @return ranked suggestions for the UI layer
     */
    public List<WordSuggestion> suggestNextWords(String previousWordText, int maxSuggestions) {
        return vocabulary.suggestNextWords(previousWordText, maxSuggestions);
    }

    /**
     * Adds a generated sentence back into the learned vocabulary.
     *
     * @param sentence sentence produced by generation or chosen by the user
     */
    public void learnFromSentence(Sentence sentence) {
        vocabulary.learnFromSentence(sentence);
    }

    /**
     * Returns generated sentences stored for duplicate checking later.
     *
     * @return unmodifiable history of generated sentences
     */
    public List<Sentence> getGeneratedSentences() {
        return Collections.unmodifiableList(generatedSentences);
    }

    /**
     * Stores a generated sentence so reports can look for duplicates.
     *
     * @param sentence newly generated sentence
     * @return the same sentence for the UI layer
     */
    private Sentence rememberGenerated(Sentence sentence) {
        generatedSentences.add(sentence);
        return sentence;
    }
}
