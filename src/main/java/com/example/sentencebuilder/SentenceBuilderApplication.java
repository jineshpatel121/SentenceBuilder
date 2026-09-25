/*
 * CS4485 Senior Design, Fall 2026
 * Sentence Builder
 * TODO: Add the submitting student's name and NetID before final submission.
 *
 * Starts the JavaFX application and displays the primary Sentence Builder window.
 */
package com.example.sentencebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SentenceBuilderApplication extends Application {
    private static final double INITIAL_WINDOW_WIDTH = 760;
    private static final double INITIAL_WINDOW_HEIGHT = 460;
    private static final double MINIMUM_WINDOW_WIDTH = 640;
    private static final double MINIMUM_WINDOW_HEIGHT = 400;

    /**
     * Loads the main FXML view and shows the application window.
     *
     * @param stage primary JavaFX stage created by the runtime
     * @throws IOException if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SentenceBuilderApplication.class.getResource("sentence-builder-view.fxml"));

        Scene scene = new Scene(
                loader.load(),
                INITIAL_WINDOW_WIDTH,
                INITIAL_WINDOW_HEIGHT);

        stage.setTitle("Sentence Builder");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
