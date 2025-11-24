# Quizzits — NetBeans Maven JavaFX

A simple flashcard & quiz app built for **CIS 1512**. It's intentionally small and heavily commented so it's easy to learn from.

## Quick Start in NetBeans
1. **File → Open Project…** and select this folder (`quizzits-netbeans`).
2. Right‑click the project → **Clean and Build**.
3. Right‑click the project → **Run** (or Run → Run Project).
   - If NetBeans asks, choose `edu.occ.quizzits.App` as the main class.
4. Deck JSON files are saved under `~/.quizzits/decks` on your computer.

## What’s included
- **JavaFX UI** (no FXML to keep it simple for beginners).
- **Models**: `Deck`, `Flashcard`, `QuizManager`.
- **Storage**: `StorageService` (uses Gson to save/load JSON).
- **Screens**: Main Menu, Create Deck, Manage Decks (add/delete cards), Start Quiz, Quiz, Results.

## Notes
- This project sticks to the SRS: local JSON files, desktop only, shuffle + scoring.
- You can extend it later (edit cards, better styling, FXML, timers, etc.).

## Maven Tips (if needed)
- Run: `mvn javafx:run`
- Clean build: `mvn clean install`
