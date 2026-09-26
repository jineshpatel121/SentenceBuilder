package com.example.sentencebuilder.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Information Expert for the in-memory word list built from imported text.
 * Written by Subom Sigdel (NetID: sxs220473) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting September 24, 2026.
 * AI-assisted draft; see docs/AI-Usage-Person2-Subom-Sigdel.md.
 */
public class Vocabulary {

    private final Map<String, Word> wordsByText = new HashMap<>();
    private final Map<String, Map<String, Integer>> followerCounts = new HashMap<>();

    /**
     * Updates word and follower statistics from one line of imported text.
     *
     * @param line one line from a source file; blank lines are ignored
     */
    public void learnFromLine(String line) {
        Objects.requireNonNull(line, "line");
        List<String> tokens = ImportTokenizer.tokenizeLine(line);
        if (tokens.isEmpty()) {
            return;
        }

        Word previousWord = null;
        for (int tokenIndex = 0; tokenIndex < tokens.size(); tokenIndex++) {
            Word currentWord = getOrCreateWord(tokens.get(tokenIndex));
            currentWord.recordOccurrence();
            if (tokenIndex == 0) {
                currentWord.recordSentenceStart();
            }
            if (tokenIndex == tokens.size() - 1) {
                currentWord.recordSentenceEnd();
            }
            if (previousWord != null) {
                recordFollower(previousWord.getText(), currentWord.getText());
            }
            previousWord = currentWord;
        }
    }

    /**
     * Returns how many distinct words are stored.
     *
     * @return vocabulary size
     */
    public int getWordCount() {
        return wordsByText.size();
    }

    /**
     * Finds a word already learned from imported text.
     *
     * @param rawText token text to look up
     * @return matching word, or null when the word is not in the vocabulary
     */
    public Word findWord(String rawText) {
        String key = Word.normalize(rawText);
        if (key.isEmpty()) {
            return null;
        }
        return wordsByText.get(key);
    }

    /**
     * Returns follower words and how often each follows the given word.
     *
     * @param wordText word to look up
     * @return map of follower text to count; empty when unknown or no followers
     */
    public Map<String, Integer> getFollowers(String wordText) {
        String key = Word.normalize(wordText);
        Map<String, Integer> followers = followerCounts.get(key);
        if (followers == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(followers);
    }

    /**
     * Returns the most common follower for a word, for simple generation tests.
     *
     * @param wordText word to look up
     * @return follower text with the highest count, or null when none exist
     */
    public String getMostCommonFollower(String wordText) {
        Map<String, Integer> followers = getFollowers(wordText);
        if (followers.isEmpty()) {
            return null;
        }
        String bestFollower = null;
        int bestCount = -1;
        for (Map.Entry<String, Integer> entry : followers.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestCount = entry.getValue();
                bestFollower = entry.getKey();
            }
        }
        return bestFollower;
    }

    /**
     * Returns a read-only view of all words keyed by normalized text.
     *
     * @return unmodifiable word map
     */
    public Map<String, Word> snapshotWords() {
        return Collections.unmodifiableMap(wordsByText);
    }

    private Word getOrCreateWord(String normalizedToken) {
        Word existing = wordsByText.get(normalizedToken);
        if (existing != null) {
            return existing;
        }
        Word created = new Word(normalizedToken);
        wordsByText.put(normalizedToken, created);
        return created;
    }

    private void recordFollower(String fromWordText, String toWordText) {
        Map<String, Integer> followers = followerCounts.computeIfAbsent(
                fromWordText,
                ignored -> new LinkedHashMap<>());
        int updatedCount = followers.getOrDefault(toWordText, 0) + 1;
        followers.put(toWordText, updatedCount);
    }
}
