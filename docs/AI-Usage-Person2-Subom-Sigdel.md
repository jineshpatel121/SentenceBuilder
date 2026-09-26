CS4485 Sentence Builder: AI-assisted work log for Person 2 (Subom Sigdel).
Each section is one session: prompt, assistant actions, human review, files, and timesheet wording.

Author: Subom Sigdel
NetID: sxs220473
Section: CS4485.0W1 Senior Design, Fall 2026
Team: 25
Project: Sentence Builder

--------------------------------------------------------------------------------

AI usage: Person 2 (Subom Sigdel), September 18, 2026

Course: CS4485.0W1 Senior Design, Fall 2026, Team 25, Sentence Builder
Work date: September 18, 2026
Tool: Cursor (AI-assisted coding in the IDE)


PROMPT

Look at this directory for information about the project: C:\Users\subha\OneDrive\Desktop\Project. I'm Person 2. This is what Person 2 does.

Person 2: Business logic and design patterns

Reading responsibilities: Read the section about reviewing functions line by line. Cover all items in the function review checklist. Read the complete Design Patterns section. Focus on Information Expert, Low Coupling, High Cohesion, and Controller.

Project responsibilities: Implement the main business logic. Create the primary information classes (sentences, words, templates, or other project data). Implement the Controller class that connects the user interface to the business logic. Keep unrelated responsibilities in separate classes. Add comments describing each function's purpose, inputs, and output. Check that loops terminate correctly. Initialize variables correctly before loops. Add exception handling where errors could occur. Make sure opened resources are closed.

Expected contribution: This person is responsible for the core operation of the program: the calculations, processing rules, and main objects.

Do not implement everything at once. For September 18, 2026, implement a very basic first pass only: small Word and Sentence information classes, a placeholder SentenceGenerator (start word only for now), and an ApplicationController for the UI layer to call later. Use the Sentence Builder repo and three-layer architecture (logic only; no JavaFX or SQL in these classes). Put module headers on my work: Subom Sigdel, NetID sxs220473, CS4485.0W1 Senior Design, Fall 2026, Team 25, Sentence Builder, starting September 18, 2026.


WHAT THE AI ASSISTANT DID

- Read materials under the desktop Project folder and the Sentence Builder repository (README, schema, existing packages).
- Created Java classes under com.example.sentencebuilder.logic: Word, Sentence, SentenceGenerator, ApplicationController.
- Added method and module comments, including author name and start date.
- Added exports com.example.sentencebuilder.logic in module-info.java.


WHAT I DID (HUMAN REVIEW)

- Submitted the prompt above and reviewed the generated code for architecture and assignment expectations.
- Confirmed scope stayed limited to a first logic-layer milestone.


FILES TOUCHED WITH AI ASSISTANCE

- src/main/java/com/example/sentencebuilder/logic/Word.java (created)
- src/main/java/com/example/sentencebuilder/logic/Sentence.java (created)
- src/main/java/com/example/sentencebuilder/logic/SentenceGenerator.java (created)
- src/main/java/com/example/sentencebuilder/logic/ApplicationController.java (created)
- src/main/java/module-info.java (export logic package)


TIMESHEET NOTE

Implemented basic Business logic and design patterns


--------------------------------------------------------------------------------

AI usage: Person 2 (Subom Sigdel), September 24, 2026

Course: CS4485.0W1 Senior Design, Fall 2026, Team 25, Sentence Builder
Work date: September 24, 2026
Tool: Cursor (AI-assisted coding in the IDE)


PROMPT

Continue incremental Person 2 work on the Sentence Builder logic layer. Review the existing September 18 classes (Word, Sentence, SentenceGenerator, ApplicationController) and the project database schema for word statistics. Implement the next small milestone only: an in-memory Vocabulary information expert that learns occurrence and sentence start/end counts from imported text lines, extend Word to hold those counts, wire learnFromLine and vocabulary size through ApplicationController, and update the placeholder SentenceGenerator so generation uses words already in the vocabulary. Do not add JDBC, JavaFX changes, word-pair probabilities, or full sentence generation yet. Document new and changed methods with purpose, inputs, and outputs.


WHAT THE AI ASSISTANT DID

- Added Vocabulary.java to learn words from one text line at a time.
- Extended Word with sentence start/end counters aligned with the schema direction.
- Updated ApplicationController with learnFromLine and getKnownWordCount.
- Updated SentenceGenerator to require a known vocabulary word for the start word.


WHAT I DID (HUMAN REVIEW)

- Submitted the prompt above and reviewed the changes for scope and three-layer separation.
- Confirmed learning and generation still run in memory with no database or UI changes.


FILES TOUCHED WITH AI ASSISTANCE

- src/main/java/com/example/sentencebuilder/logic/Vocabulary.java (created)
- src/main/java/com/example/sentencebuilder/logic/Word.java (extended)
- src/main/java/com/example/sentencebuilder/logic/ApplicationController.java (updated)
- src/main/java/com/example/sentencebuilder/logic/SentenceGenerator.java (updated)


TIMESHEET NOTE

Extended in-memory vocabulary learning in the business logic layer


--------------------------------------------------------------------------------

AI usage: Person 2 (Subom Sigdel), September 26, 2026

Course: CS4485.0W1 Senior Design, Fall 2026, Team 25, Sentence Builder
Work date: September 26, 2026
Tool: Cursor (AI-assisted coding in the IDE)


PROMPT

Continue Person 2 logic-layer work for September 26, 2026. Address the next incremental step after in-memory vocabulary learning: document a first-pass tokenization policy for imported text lines, record word-to-word follower counts in Vocabulary, and extend the placeholder SentenceGenerator to append at most one most-common follower to the start word. Keep the change set small; do not add JDBC, JavaFX, weighted random generation, or autocomplete yet. Add method comments describing purpose, inputs, and outputs.


WHAT THE AI ASSISTANT DID

- Added ImportTokenizer with documented edge-punctuation rules for line tokenization.
- Updated Vocabulary to use ImportTokenizer, store follower counts, and expose getFollowers and getMostCommonFollower.
- Updated SentenceGenerator to build up to a two-word sentence using the most common follower.


WHAT I DID (HUMAN REVIEW)

- Submitted the prompt above and reviewed the changes against the planned parsing and follower milestone.
- Confirmed follower counts and tokenization stay in the logic layer only.


FILES TOUCHED WITH AI ASSISTANCE

- src/main/java/com/example/sentencebuilder/logic/ImportTokenizer.java (created)
- src/main/java/com/example/sentencebuilder/logic/Vocabulary.java (updated)
- src/main/java/com/example/sentencebuilder/logic/SentenceGenerator.java (updated)


TIMESHEET NOTE

Added import tokenization rules and basic word-follower tracking in the logic layer
