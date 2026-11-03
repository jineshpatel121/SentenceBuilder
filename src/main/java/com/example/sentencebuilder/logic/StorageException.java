package com.example.sentencebuilder.logic;

/**
 * Unchecked error raised when saving or loading data fails.
 * Written by Merit Bhusal (NetID: mxb220125) for CS4485.0W1 Senior Design,
 * Fall 2026, Team 25, Sentence Builder, starting October 28, 2026.
 *
 * <p>This lives in the logic package so the data layer can throw it and the logic and
 * ui layers can catch it without ever seeing a JDBC type such as SQLException.
 */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an error with a message and the underlying cause.
     *
     * @param message what the application was trying to do when it failed
     * @param cause the underlying error, for example a SQLException
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
