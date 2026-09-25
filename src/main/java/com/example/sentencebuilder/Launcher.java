/*
 * CS4485 Senior Design, Fall 2026
 * Sentence Builder
 * TODO: Add the submitting student's name and NetID before final submission.
 *
 * Provides the executable entry point used by Maven and packaged builds.
 */
package com.example.sentencebuilder;

import javafx.application.Application;

public class Launcher {

    /**
     * Starts the JavaFX runtime with the Sentence Builder application class.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(SentenceBuilderApplication.class, args);
    }
}
