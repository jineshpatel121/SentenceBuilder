# Assigned Scope Progress

This branch contains a self-contained development slice covering the assigned topics:
database design details, event-driven JavaFX behavior, background processing,
user-experience safeguards, and maintainability practices.

## Milestone 1 - Database design foundation

- Added a MySQL/MariaDB schema for words, word-to-word relationships, imported files,
  and generated sentence history.
- Used surrogate numeric keys for referenced records.
- Chose variable-length text types for data whose length is not naturally fixed.
- Added counts needed by generation and auto-complete features without placing database
  behavior in the JavaFX controller.

## Milestone 2 - Background import foundation

- Added validation for user-selected text files.
- Added a side-effect-free text-file analyzer.
- Added a JavaFX Task wrapper so large file reads run off the JavaFX application thread.
- Added progress and cancellation support.
- Kept token counting explicitly separate from the team's final definition of a word so
  parsing policy can be integrated without rewriting the UI/threading layer.

## Milestone 3 - Event-driven UI and usability

- Replaced the starter Hello screen with a Sentence Builder import screen.
- Added Browse, Import, and Cancel event handlers.
- Disabled invalid actions until input is valid.
- Added keyboard mnemonics and a default Import action.
- Added user-facing validation, progress, completion, cancellation, and error messages.
- Moved repeated values into named constants and kept file analysis free of external side effects.
- Added method/class documentation for the new Java code.

## Verification added

JUnit tests cover file validation, file analysis counts, progress reporting, and cancellation.

## Integration intentionally left for the shared team implementation

The analyzer currently reads and measures the selected text file but does not write parsed
words to MySQL. This avoids duplicating or conflicting with the repository/CRUD implementation
being developed in another workstream. Once that API is available, the worker task can call it
after parsing while retaining the same validation, progress, cancellation, and UI behavior.

The project's final definition of a "word" is also intentionally not hard-coded here because
that is a shared parsing/data-model decision.
