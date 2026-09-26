package com.example.sentencebuilder.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Will generate sentences from word-probability data in the corpus/database.
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting September 18, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public class SentenceGenerator {

    /**
     * Builds a short sentence from the start word plus one likely follower when available.
     * Weighted random selection will replace the most-common rule in a later milestone.
     *
     * @param vocabulary learned words from imported text
     * @param startWordText word chosen by the user
     * @return sentence with the start word and optionally one follower
     * @throws IllegalArgumentException when the start word is not in the vocabulary
     */
    public Sentence generateFromStartWord(Vocabulary vocabulary, String startWordText) {
        Objects.requireNonNull(vocabulary, "vocabulary");
        Word startWord = vocabulary.findWord(startWordText);
        if (startWord == null) {
            throw new IllegalArgumentException(
                    "The vocabulary does not contain the starting word: " + startWordText);
        }
        List<Word> generatedWords = new ArrayList<>(2);
        generatedWords.add(startWord);

        String followerText = vocabulary.getMostCommonFollower(startWord.getText());
        if (followerText != null) {
            Word followerWord = vocabulary.findWord(followerText);
            if (followerWord != null) {
                generatedWords.add(followerWord);
            }
        }

        return new Sentence(generatedWords);
    }
}
