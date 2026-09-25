/*
 * CS4485 Senior Design, Fall 2026
 * Sentence Builder
 * TODO: Add the submitting student's name and NetID before final submission.
 *
 * Handles event-driven behavior for the current import screen. Long file reads
 * are delegated to TextImportTask so the JavaFX application thread stays responsive.
 */
package com.example.sentencebuilder.ui;

import com.example.sentencebuilder.logic.ImportFileValidator;
import com.example.sentencebuilder.logic.ImportSummary;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class SentenceBuilderController {
    private static final String WORKER_THREAD_NAME = "sentence-builder-text-import";

    @FXML
    private TextField filePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button importButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ProgressBar importProgressBar;

    @FXML
    private Label validationLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea summaryArea;

    private TextImportTask activeTask;

    /**
     * Establishes initial UI state and validates the file field whenever it changes.
     */
    @FXML
    public void initialize() {
        filePathField.textProperty().addListener(
                (observable, oldValue, newValue) -> refreshValidation());

        importProgressBar.setProgress(0);
        cancelButton.setDisable(true);
        statusLabel.setText("Ready.");
        summaryArea.setText("No text file has been read yet.");
        refreshValidation();
    }

    /**
     * Opens a file chooser after the user presses Browse.
     */
    @FXML
    protected void onBrowseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a text file");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));

        java.io.File selectedFile = chooser.showOpenDialog(
                filePathField.getScene().getWindow());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Starts a validated file read on a background thread after the user presses Import.
     */
    @FXML
    protected void onImportFile() {
        Path sourceFile = getValidatedPath();
        if (sourceFile == null) {
            return;
        }

        TextImportTask task = new TextImportTask(sourceFile);
        activeTask = task;
        setBusy(true);

        importProgressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(event -> {
            ImportSummary summary = task.getValue();
            finishTask();
            importProgressBar.setProgress(1);
            statusLabel.setText("Finished reading " + summary.sourceFile().getFileName() + ".");
            summaryArea.setText(formatSummary(summary));
        });

        task.setOnFailed(event -> {
            Throwable failure = task.getException();
            finishTask();
            importProgressBar.setProgress(0);
            statusLabel.setText("Import read failed.");
            summaryArea.setText(formatFailure(failure));
        });

        task.setOnCancelled(event -> {
            finishTask();
            importProgressBar.setProgress(0);
            statusLabel.setText("File read cancelled.");
        });

        Thread worker = new Thread(task, WORKER_THREAD_NAME);
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * Requests cancellation of the active background file read.
     */
    @FXML
    protected void onCancelImport() {
        if (activeTask != null) {
            activeTask.cancel();
        }
    }

    /**
     * Converts the current path field to a Path only when validation succeeds.
     *
     * @return valid import path, or null when the field is invalid
     */
    private Path getValidatedPath() {
        try {
            Path path = Path.of(filePathField.getText().trim());
            String validationMessage = ImportFileValidator.validate(path);
            if (!validationMessage.isEmpty()) {
                validationLabel.setText(validationMessage);
                importButton.setDisable(true);
                return null;
            }
            return path;
        } catch (InvalidPathException exception) {
            validationLabel.setText("The file path is not valid.");
            importButton.setDisable(true);
            return null;
        }
    }

    /**
     * Updates validation feedback and prevents Import until the input is usable.
     */
    private void refreshValidation() {
        if (activeTask != null) {
            importButton.setDisable(true);
            return;
        }

        String rawPath = filePathField.getText().trim();
        if (rawPath.isEmpty()) {
            validationLabel.setText("Choose or type a plain-text (.txt) file.");
            importButton.setDisable(true);
            return;
        }

        try {
            String validationMessage = ImportFileValidator.validate(Path.of(rawPath));
            validationLabel.setText(
                    validationMessage.isEmpty()
                            ? "File is ready to read."
                            : validationMessage);
            importButton.setDisable(!validationMessage.isEmpty());
        } catch (InvalidPathException exception) {
            validationLabel.setText("The file path is not valid.");
            importButton.setDisable(true);
        }
    }

    /**
     * Enables or disables controls that should not change while a file is being read.
     *
     * @param busy true while a background task is active
     */
    private void setBusy(boolean busy) {
        filePathField.setDisable(busy);
        browseButton.setDisable(busy);
        importButton.setDisable(busy);
        cancelButton.setDisable(!busy);
    }

    /**
     * Removes task bindings and restores controls after success, failure, or cancellation.
     */
    private void finishTask() {
        importProgressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();
        activeTask = null;
        setBusy(false);
        refreshValidation();
    }

    /**
     * Builds a compact summary of the completed read for the user.
     *
     * @param summary immutable result returned by the background task
     * @return formatted, user-facing summary
     */
    private String formatSummary(ImportSummary summary) {
        return String.format(
                "File: %s%n"
                        + "Lines read: %,d%n"
                        + "Tokens read: %,d%n"
                        + "File size: %,d bytes%n"
                        + "Elapsed time: %,d ms%n%n"
                        + "The token count is only a read metric. "
                        + "The team's final definition of a word can remain in the parsing/database logic.",
                summary.sourceFile().toAbsolutePath(),
                summary.lineCount(),
                summary.tokenCount(),
                summary.fileSizeBytes(),
                summary.elapsedMilliseconds());
    }

    /**
     * Produces a useful error message without exposing a stack trace in the UI.
     *
     * @param failure exception raised by the worker task
     * @return user-facing error text
     */
    private String formatFailure(Throwable failure) {
        if (failure == null || failure.getMessage() == null || failure.getMessage().isBlank()) {
            return "The text file could not be read.";
        }
        return "The text file could not be read: " + failure.getMessage();
    }
}
