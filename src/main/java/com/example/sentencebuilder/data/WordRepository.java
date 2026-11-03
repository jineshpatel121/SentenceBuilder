package com.example.sentencebuilder.data;

import com.example.sentencebuilder.logic.GenerationMethod;
import com.example.sentencebuilder.logic.ImportSummary;
import com.example.sentencebuilder.logic.Sentence;
import com.example.sentencebuilder.logic.StorageException;
import com.example.sentencebuilder.logic.Vocabulary;
import com.example.sentencebuilder.logic.VocabularyRepository;
import com.example.sentencebuilder.logic.Word;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stores and loads Sentence Builder data in MySQL/MariaDB using JDBC.
 * Written by Merit Bhusal (NetID: mxb220125) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting October 28, 2026.
 *
 * <p>This is the technical-services implementation of {@link VocabularyRepository}.
 * It follows the contract's rules: every method opens its own connection and closes it
 * before returning, SQLException never escapes (it is wrapped in {@link StorageException}),
 * and whole objects are passed in rather than loose fields. All SQL lives here, never in
 * the ui or logic layers. The tables are defined in resources/database/schema.sql.
 */
public class WordRepository implements VocabularyRepository {

    /** Matches words.word_text VARCHAR(100) in schema.sql. */
    private static final int MAX_WORD_LENGTH = 100;

    /** Rows sent to the database per batch while saving a large vocabulary. */
    private static final int BATCH_SIZE = 1000;

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/sentence_builder?rewriteBatchedStatements=true";
    private static final String DEFAULT_USER = "root";

    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a repository that connects with the given settings.
     * No connection is opened until a method is called.
     *
     * @param url JDBC URL, for example jdbc:mysql://localhost:3306/sentence_builder
     * @param user database user name
     * @param password database password; may be empty
     */
    public WordRepository(String url, String user, String password) {
        this.url = Objects.requireNonNull(url, "url");
        this.user = Objects.requireNonNull(user, "user");
        this.password = Objects.requireNonNull(password, "password");
    }

    /**
     * Creates a repository from environment variables so no password is stored in the
     * code. SB_DB_URL, SB_DB_USER and SB_DB_PASSWORD are read; anything not set falls
     * back to a local database named sentence_builder, user root, and an empty password.
     *
     * @return repository using the configured settings
     */
    public static WordRepository fromEnvironment() {
        return new WordRepository(
                valueOrDefault(System.getenv("SB_DB_URL"), DEFAULT_URL),
                valueOrDefault(System.getenv("SB_DB_USER"), DEFAULT_USER),
                valueOrDefault(System.getenv("SB_DB_PASSWORD"), ""));
    }

    /**
     * Saves every word and follower count, replacing the stored totals. Runs as one
     * transaction, so a failure part-way leaves the old data untouched. Words longer
     * than the 100-character column are skipped, along with their follower links.
     *
     * @param vocabulary learned words and follower counts to store
     */
    @Override
    public void saveVocabulary(Vocabulary vocabulary) {
        Objects.requireNonNull(vocabulary, "vocabulary");
        Map<String, Word> words = vocabulary.snapshotWords();

        try (Connection connection = open()) {
            connection.setAutoCommit(false);
            try {
                saveWords(connection, words);
                Map<String, Long> wordIds = loadWordIds(connection);
                saveFollowers(connection, vocabulary, words, wordIds);
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                connection.rollback();
                throw failure;
            }
        } catch (SQLException exception) {
            throw failed("save the vocabulary", exception);
        }
    }

    /**
     * Rebuilds the vocabulary from the stored words and follower counts.
     *
     * @return vocabulary holding everything stored; empty when nothing is stored yet
     */
    @Override
    public Vocabulary loadVocabulary() {
        Vocabulary vocabulary = new Vocabulary();
        Map<Long, String> textById = new HashMap<>();

        try (Connection connection = open();
             Statement statement = connection.createStatement()) {

            try (ResultSet rows = statement.executeQuery(
                    "SELECT word_id, word_text, occurrence_count, "
                            + "sentence_start_count, sentence_end_count FROM words")) {
                while (rows.next()) {
                    String text = rows.getString("word_text");
                    textById.put(rows.getLong("word_id"), text);
                    vocabulary.restoreWord(
                            text,
                            rows.getLong("occurrence_count"),
                            rows.getLong("sentence_start_count"),
                            rows.getLong("sentence_end_count"));
                }
            }

            // Pairs with a zero import count (created only by autocomplete choices that
            // have not been saved back yet) are skipped: a follower weight must be positive.
            try (ResultSet rows = statement.executeQuery(
                    "SELECT word_id, following_word_id, occurrence_count "
                            + "FROM word_followers WHERE occurrence_count > 0")) {
                while (rows.next()) {
                    String from = textById.get(rows.getLong("word_id"));
                    String to = textById.get(rows.getLong("following_word_id"));
                    if (from != null && to != null) {
                        long count = Math.min(rows.getLong("occurrence_count"), Integer.MAX_VALUE);
                        vocabulary.restoreFollower(from, to, (int) count);
                    }
                }
            }
        } catch (SQLException exception) {
            throw failed("load the vocabulary", exception);
        }
        return vocabulary;
    }

    /**
     * Records one imported file: name, full path, word count, size, and import time.
     *
     * @param summary result of analyzing one imported file
     * @return identifier of the new imported-file record
     */
    @Override
    public long recordImportedFile(ImportSummary summary) {
        Objects.requireNonNull(summary, "summary");
        String sql = "INSERT INTO imported_files (file_name, file_path, word_count, file_size_bytes) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = open();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, String.valueOf(summary.sourceFile().getFileName()));
            statement.setString(2, summary.sourceFile().toAbsolutePath().toString());
            statement.setLong(3, summary.tokenCount());
            statement.setLong(4, summary.fileSizeBytes());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new StorageException("The database did not return an id for the imported file.", null);
        } catch (SQLException exception) {
            throw failed("record the imported file", exception);
        }
    }

    /**
     * Stores a generated sentence so reports can look for duplicates.
     *
     * @param sentence sentence produced by a generation algorithm
     * @param method algorithm that produced the sentence
     */
    @Override
    public void saveGeneratedSentence(Sentence sentence, GenerationMethod method) {
        Objects.requireNonNull(sentence, "sentence");
        Objects.requireNonNull(method, "method");
        String sql = "INSERT INTO generated_sentences "
                + "(sentence_text, starting_word_id, generation_method) VALUES (?, ?, ?)";

        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Long startingWordId = findWordId(connection, sentence.getWords().get(0).getText());

            statement.setString(1, sentence.asText());
            if (startingWordId == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setLong(2, startingWordId);
            }
            statement.setString(3, method.name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw failed("save the generated sentence", exception);
        }
    }

    /**
     * Counts one autocomplete choice the same way {@code Vocabulary.recordUserChoice} does
     * in memory: the chosen word and the pair (previous, chosen) each get one more
     * occurrence and one more user choice. Words not stored yet are added first. Because
     * the stored counts stay in step with memory, the whole vocabulary does not need to be
     * saved again after each choice.
     *
     * @param previousWordText completed word before the cursor
     * @param chosenWordText word the user chose or typed next
     */
    @Override
    public void recordUserChoice(String previousWordText, String chosenWordText) {
        String previous = requireStorableWord(previousWordText, "previousWordText");
        String chosen = requireStorableWord(chosenWordText, "chosenWordText");

        try (Connection connection = open()) {
            connection.setAutoCommit(false);
            try {
                long previousId = ensureWord(connection, previous);
                long chosenId = ensureWord(connection, chosen);

                try (PreparedStatement pair = connection.prepareStatement(
                        "INSERT INTO word_followers "
                                + "(word_id, following_word_id, occurrence_count, user_choice_count) "
                                + "VALUES (?, ?, 1, 1) "
                                + "ON DUPLICATE KEY UPDATE occurrence_count = occurrence_count + 1, "
                                + "user_choice_count = user_choice_count + 1")) {
                    pair.setLong(1, previousId);
                    pair.setLong(2, chosenId);
                    pair.executeUpdate();
                }
                try (PreparedStatement word = connection.prepareStatement(
                        "UPDATE words SET occurrence_count = occurrence_count + 1, "
                        + "user_choice_count = user_choice_count + 1 WHERE word_id = ?")) {
                    word.setLong(1, chosenId);
                    word.executeUpdate();
                }
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                connection.rollback();
                throw failure;
            }
        } catch (SQLException exception) {
            throw failed("record the autocomplete choice", exception);
        }
    }

    // ---- helpers -----------------------------------------------------------------

    /**
     * Opens a new connection. Callers close it with try-with-resources.
     *
     * @return open connection
     * @throws SQLException if the database cannot be reached
     */
    private Connection open() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Adds or updates every word that fits in the word column.
     *
     * @param connection open connection inside a transaction
     * @param words words to store, keyed by text
     * @throws SQLException if a statement fails
     */
    private void saveWords(Connection connection, Map<String, Word> words) throws SQLException {
        String sql = "INSERT INTO words "
                + "(word_text, occurrence_count, sentence_start_count, sentence_end_count) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE occurrence_count = VALUES(occurrence_count), "
                + "sentence_start_count = VALUES(sentence_start_count), "
                + "sentence_end_count = VALUES(sentence_end_count)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int pending = 0;
            for (Word word : words.values()) {
                if (!fitsColumn(word.getText())) {
                    continue;
                }
                statement.setString(1, word.getText());
                statement.setLong(2, word.getOccurrenceCount());
                statement.setLong(3, word.getSentenceStartCount());
                statement.setLong(4, word.getSentenceEndCount());
                statement.addBatch();
                if (++pending % BATCH_SIZE == 0) {
                    statement.executeBatch();
                }
            }
            statement.executeBatch();
        }
    }

    /**
     * Adds or updates every follower pair whose two words are stored.
     *
     * @param connection open connection inside a transaction
     * @param vocabulary source of follower counts
     * @param words every word in the vocabulary, keyed by text
     * @param wordIds database id for each stored word text; may gain entries here
     * @throws SQLException if a statement fails
     */
    private void saveFollowers(
            Connection connection,
            Vocabulary vocabulary,
            Map<String, Word> words,
            Map<String, Long> wordIds) throws SQLException {
        String sql = "INSERT INTO word_followers (word_id, following_word_id, occurrence_count) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE occurrence_count = VALUES(occurrence_count)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int pending = 0;
            for (String fromText : words.keySet()) {
                Long fromId = idFor(connection, wordIds, fromText);
                if (fromId == null) {
                    continue;
                }
                for (Map.Entry<String, Integer> follower
                        : vocabulary.getFollowers(fromText).entrySet()) {
                    Long toId = idFor(connection, wordIds, follower.getKey());
                    if (toId == null) {
                        continue;
                    }
                    statement.setLong(1, fromId);
                    statement.setLong(2, toId);
                    statement.setLong(3, follower.getValue());
                    statement.addBatch();
                    if (++pending % BATCH_SIZE == 0) {
                        statement.executeBatch();
                    }
                }
            }
            statement.executeBatch();
        }
    }

    /**
     * Reads every stored word id in one query.
     *
     * @param connection open connection
     * @return word text mapped to its database id
     * @throws SQLException if the query fails
     */
    private Map<String, Long> loadWordIds(Connection connection) throws SQLException {
        Map<String, Long> ids = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT word_id, word_text FROM words")) {
            while (rows.next()) {
                ids.put(rows.getString("word_text"), rows.getLong("word_id"));
            }
        }
        return ids;
    }

    /**
     * Finds a word's id, falling back to a direct lookup so the database's own text
     * comparison decides what counts as the same word (for example accented letters).
     *
     * @param connection open connection
     * @param wordIds ids already loaded; updated when the fallback finds one
     * @param text word text
     * @return database id, or null when the word is not stored (or too long to store)
     * @throws SQLException if the lookup fails
     */
    private Long idFor(Connection connection, Map<String, Long> wordIds, String text)
            throws SQLException {
        Long id = wordIds.get(text);
        if (id == null && fitsColumn(text)) {
            id = findWordId(connection, text);
            if (id != null) {
                wordIds.put(text, id);
            }
        }
        return id;
    }

    /**
     * Looks up one word's id.
     *
     * @param connection open connection
     * @param text word text
     * @return database id, or null when the word is not stored
     * @throws SQLException if the query fails
     */
    private Long findWordId(Connection connection, String text) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT word_id FROM words WHERE word_text = ?")) {
            statement.setString(1, text);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? rows.getLong(1) : null;
            }
        }
    }

    /**
     * Returns a word's id, adding the word with zero counts if it is not stored yet.
     *
     * @param connection open connection inside a transaction
     * @param text normalized word text
     * @return database id of the word
     * @throws SQLException if a statement fails
     */
    private long ensureWord(Connection connection, String text) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO words (word_text) VALUES (?) "
                        + "ON DUPLICATE KEY UPDATE word_text = word_text")) {
            insert.setString(1, text);
            insert.executeUpdate();
        }
        Long id = findWordId(connection, text);
        if (id == null) {
            throw new SQLException("Word was not found right after it was added: " + text);
        }
        return id;
    }

    /**
     * Normalizes a word for storage and rejects text that cannot be stored.
     *
     * @param rawText word text from the caller
     * @param argumentName argument name used in the error message
     * @return normalized word
     */
    private static String requireStorableWord(String rawText, String argumentName) {
        Objects.requireNonNull(rawText, argumentName);
        String normalized = Word.normalize(rawText);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(argumentName + " cannot be blank.");
        }
        if (!fitsColumn(normalized)) {
            throw new IllegalArgumentException(
                    argumentName + " is longer than " + MAX_WORD_LENGTH + " characters.");
        }
        return normalized;
    }

    private static boolean fitsColumn(String text) {
        return text.length() <= MAX_WORD_LENGTH;
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static StorageException failed(String action, SQLException cause) {
        return new StorageException(
                "Could not " + action + ": " + cause.getMessage(), cause);
    }
}
