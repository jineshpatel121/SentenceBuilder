/*
 * CS4485 Senior Design, Fall 2026
 * Sentence Builder
 * TODO: Add the submitting student's name and NetID before final submission.
 *
 * Runs text-file analysis away from the JavaFX application thread and exposes
 * progress and status messages to the user interface.
 */
package com.example.sentencebuilder.ui;

import com.example.sentencebuilder.logic.ImportSummary;
import com.example.sentencebuilder.logic.TextFileAnalyzer;
import javafx.concurrent.Task;

import java.nio.file.Path;

public class TextImportTask extends Task<ImportSummary> {
    private final Path sourceFile;
    private final TextFileAnalyzer analyzer;

    /**
     * Creates a task for one user-selected text file.
     *
     * @param sourceFile readable text file selected by the user
     */
    public TextImportTask(Path sourceFile) {
        this.sourceFile = sourceFile;
        this.analyzer = new TextFileAnalyzer();
    }

    /**
     * Performs the file read on a worker thread and forwards progress to JavaFX.
     *
     * @return summary of the completed file read
     * @throws Exception if the source file cannot be read
     */
    @Override
    protected ImportSummary call() throws Exception {
        updateTitle("Reading text file");
        updateMessage("Preparing " + sourceFile.getFileName() + "...");

        return analyzer.analyze(
                sourceFile,
                this::isCancelled,
                (completedLines, totalLines) -> {
                    if (totalLines == 0) {
                        updateProgress(1, 1);
                    } else {
                        updateProgress(completedLines, totalLines);
                    }

                    updateMessage(String.format(
                            "Reading %s: %,d of %,d lines",
                            sourceFile.getFileName(),
                            completedLines,
                            totalLines));
                });
    }
}
