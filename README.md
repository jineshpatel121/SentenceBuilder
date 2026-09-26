# Sentence Builder

CS4485 Senior Design — Fall 2026 (Prof. John Cole)

A JavaFX application that reads text files, builds a word-frequency database, and generates new sentences using word-probability algorithms and auto-complete.

## Tech stack

- **Language:** Java
- **UI:** JavaFX
- **Database:** MySQL / MariaDB
- **Build tool:** Maven
- **IDE:** IntelliJ IDEA (Community Edition) recommended

## Project structure

The codebase follows a three-layer architecture:

```
src/main/java/com/example/sentencebuilder/
├── ui/       → JavaFX views, controllers, FXML
├── logic/    → Business logic, sentence generation, Controller
├── data/     → Database access, file import/parsing
```

Information flows: **UI → Business logic → Technical services → MySQL database**, with results returned back up through the same layers.

## Getting started

1. Clone the repo:
   ```
   git clone https://github.com/s1gdel/SentenceBuilder.git
   ```
2. Open the folder in IntelliJ IDEA.
3. Let Maven download dependencies (JavaFX is pulled in automatically via `pom.xml`).
4. Set the Project SDK if prompted (JDK 17+ recommended).
5. Run `HelloApplication.java` to confirm your environment works.

## Team roles

| Person | Responsibility |
|--------|----------------|
| Person 1 | Program structure, three-layer architecture, initial design |
| Person 2 | Business logic, design patterns, Controller |
| Person 3 | Data storage, database/file access (technical services) |
| Person 4 | User interface, events, testing, user experience |

## Contributing

- Work on a feature branch, not directly on `main`.
- Open a pull request for review before merging.
- Keep each class focused on a single responsibility.

## Status

🚧 In progress — project scaffold and layer structure set up. Database schema, sentence generation, and UI still to come.