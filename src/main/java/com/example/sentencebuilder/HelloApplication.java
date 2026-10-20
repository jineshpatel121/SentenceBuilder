package com.example.sentencebuilder;

import com.example.sentencebuilder.logic.ApplicationController;
import com.example.sentencebuilder.ui.ImportController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Starts the JavaFX application and displays the current Sentence Builder screen.
 *
 * <p>This class is the one place where the layers are assembled: it builds the
 * ApplicationController and hands it to the screen, so the UI never creates
 * business-logic or data objects itself.
 */
public class HelloApplication extends Application {
    private static final double WINDOW_WIDTH = 720;
    private static final double WINDOW_HEIGHT = 420;

    /**
     * Builds the controller, loads the file-analysis view, connects the two, and
     * shows the primary application window.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Person 3: once WordRepository implements VocabularyRepository, pass it here,
        // for example new ApplicationController(new WordRepository(...)).
        ApplicationController applicationController = new ApplicationController();

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("import-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        // FXML creates the screen's controller itself, so hand it the logic controller afterward.
        ImportController importController = fxmlLoader.getController();
        importController.setApplicationController(applicationController);

        stage.setTitle("Sentence Builder");
        stage.setScene(scene);
        stage.show();
    }
}