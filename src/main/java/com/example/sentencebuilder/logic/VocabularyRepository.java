package com.example.sentencebuilder.logic;

/**
 * Contract between the business-logic layer and the technical-services layer.
 * Written by Merit Bhusal (NetID: mxb220125) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting October 19, 2026.
 *
 * <p>The logic layer (ApplicationController) talks only to this interface. It never
 * knows whether data lives in MySQL, a file, or memory. The data layer supplies the
 * implementation, for example WordRepository using JDBC.
 *
 * <p>This interface lives in logic/ on purpose. Implementations need logic classes
 * such as Vocabulary and Sentence, so the dependency points one way: data depends on
 * logic. If the interface lived in data/, the two packages would depend on each other.
 *
 * <p>Rules every implementation should follow:
 * <ul>
 *   <li>Open a database connection only for one call and close it before returning.</li>
 *   <li>Do not let SQLException escape. Wrap it in an unchecked exception so the logic
 *       and ui layers never see JDBC types. The team should agree on which exception.</li>
 *   <li>Accept complete objects rather than loose fields, wherever an object exists.</li>
 * </ul>
 */
public interface VocabularyRepository {

    /**
     * Saves every word and follower count in the vocabulary.
     * Stored counts are replaced by the totals in the given vocabulary, so the
     * vocabulary should be loaded from this repository before new text is imported.
     *
     * @param vocabulary learned words and follower counts to store
     */
    void saveVocabulary(Vocabulary vocabulary);

    /**
     * Rebuilds the vocabulary from stored data, for use at application startup.
     * Vocabulary can currently only learn from text lines, so it needs a way to
     * restore stored counts before this can be implemented.
     *
     * @return vocabulary holding all stored words; empty when nothing is stored yet
     */
    Vocabulary loadVocabulary();

    /**
     * Records what was imported: file name, path, word count, size, and import time.
     *
     * @param summary result of analyzing one imported file
     * @return identifier of the new imported-file record
     */
    long recordImportedFile(ImportSummary summary);

    /**
     * Stores a generated sentence so reports can look for duplicates.
     *
     * @param sentence sentence produced by a generation algorithm
     * @param method algorithm that produced the sentence
     */
    void saveGeneratedSentence(Sentence sentence, GenerationMethod method);

    /**
     * Records that the user picked a word as the follower of another word in
     * autocomplete. A chosen word that is not stored yet is added first.
     *
     * @param previousWordText completed word before the cursor
     * @param chosenWordText word the user chose or typed next
     */
    void recordUserChoice(String previousWordText, String chosenWordText);
}