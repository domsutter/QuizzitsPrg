/*
 * =======================  STUDENT NOTES  =======================
 * File: ScreenRouter.java
 * What is this?  --> This file is part of the QuizGame app.
 * Why do we care? --> We're practicing reading Java + JavaFX projects.
 *
 * Reading tips:
 *  1) Skim class names and fields first.
 *  2) Read method headers to see inputs/outputs.
 *  3) Only then read the body and follow the comments.
 *
 * TODO(you): As you read, write down 1-2 questions per method.
 * Hint: If something looks "magical", search where it's created/used.
 * ===============================================================
 */

package quizgame;
import quizgame.util.StudentLogger;  // helper for step-by-step logs

import quizgame.model.Deck;
import quizgame.model.Flashcard;
import quizgame.model.QuizManager;
import quizgame.storage.StorageService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


/**
 * ScreenRouter
 * ------------
 * Central place that builds center content for each page and wraps it
 * with the mac-style shell so all screens have the same size/layout.
 */
public class ScreenRouter {

    private final StorageService storage = new StorageService();  // Object creation: why do we need a new instance here?

    // HOME: prefer sidebar home scene (welcome)
/**
 * STUDENT NOTE: What does `getPrimaryScene()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    public Scene getPrimaryScene(Stage stage) {
        StudentLogger.enter("names#getPrimaryScene");  // we just entered this method
        // Use the SidebarMain home view (which already applies Styles).
        try {
            StudentLogger.step("About to return from method.");  // check what value we return next
            return new SidebarMain(this).build(stage);  // Return value: where is it used next?
        } catch (Exception ignored) {
}
        // Fallback: simple label inputFile the shell
        VBox center = new VBox(10);  // Object creation: why do we need a new instance here?
        center.setPadding(new Insets(24));
        center.getChildren().add(new Label("Welcome to Quizzits"));  // We're adding to a collection; note the order & size.
        MacShell shell = new MacShell(this, stage);  // Object creation: why do we need a new instance here?
        StudentLogger.step("About to return from method.");  // check what value we return next
        return shell.sceneWith(center);  // Return value: where is it used next?
    }

/**
 * STUDENT NOTE: What does `createDeckScene()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    public Scene createDeckScene(Stage stage) {
        StudentLogger.enter("names#createDeckScene");  // we just entered this method
        VBox center = new VBox(12);  // Object creation: why do we need a new instance here?
        Label title = new Label("Create Deck");  // Object creation: why do we need a new instance here?
        title.getStyleClass().add("headline");  // We're adding to a collection; note the order & size.
        TextField deckName = new TextField();  // Object creation: why do we need a new instance here?
        deckName.setPromptText("Enter deckObject name (must be unique)");

        HBox buttons = new HBox(8);  // Object creation: why do we need a new instance here?
        Button create = new Button("Create");  // Object creation: why do we need a new instance here?
        Button back = new Button("Back");  // Object creation: why do we need a new instance here?
        buttons.getChildren().addAll(create, back);

        Label status = new Label("");  // Object creation: why do we need a new instance here?

        create.setOnAction(exceptionObj -> {
            String name = deckName.getText().trim();
            if (name.isEmpty()) {  // decision point — what happens inputFile each branch?
                status.setText("Deck name cannot be empty.");
                StudentLogger.step("About to return from method.");  // check what value we return next
                return;
            }
            if (storage.deckExists(name)) {  // decision point — what happens inputFile each branch?
                status.setText("A deckObject with that name already exists.");
                StudentLogger.step("About to return from method.");  // check what value we return next
                return;
            }
            Deck d = new Deck(name, new ArrayList<>());  // Object creation: why do we need a new instance here?
            boolean ok = storage.saveDeck(d);
            status.setText(ok ? "Deck created successfully." : "Failed to save deckObject.");
        });

        back.setOnAction(exceptionObj -> stage.setScene(getPrimaryScene(stage)));

        center.getChildren().addAll(title, deckName, buttons, status);
        MacShell shell = new MacShell(this, stage);  // Object creation: why do we need a new instance here?
        StudentLogger.step("About to return from method.");  // check what value we return next
        return shell.sceneWith(center);  // Return value: where is it used next?
    }

/**
 * STUDENT NOTE: What does `manageDecksScene()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    public Scene manageDecksScene(Stage stage) {
    BorderPane center = new BorderPane();

    // Left: list of decks
    ListView<String> deckList = new ListView<>();
    deckList.setItems(FXCollections.observableArrayList(storage.listDeckNames()));
    center.setLeft(deckList);

    // Right: list of cards for selected deck
    ListView<String> cardList = new ListView<>();
    center.setCenter(cardList);

    // --- inputs (Row 1) ---
    TextField qField = new TextField();
    qField.setPromptText("Question");
    TextField aField = new TextField();
    aField.setPromptText("Answer");

    HBox inputsRow = new HBox(8);
    inputsRow.getChildren().addAll(qField, aField);
    HBox.setHgrow(qField, Priority.ALWAYS);
    HBox.setHgrow(aField, Priority.ALWAYS);

    // --- buttons (Row 2) ---
    Button add  = new Button("Add Card");
    Button edit = new Button("Edit Card");
    Button del  = new Button("Delete Card");
    Button back = new Button("Back");

    // Make labels readable (avoid truncation)
    Button[] btns = { add, edit, del, back };
    for (Button b : btns) b.setMinWidth(100);

    HBox buttonsRow = new HBox(10);
    buttonsRow.setAlignment(Pos.CENTER_LEFT);
    buttonsRow.getChildren().addAll(add, edit, del, back);

    // Stack rows vertically at the top
    VBox top = new VBox(10);
    top.getChildren().addAll(inputsRow, buttonsRow);
    top.setPadding(new Insets(10));
    center.setTop(top);

    // When a deck is selected, show its cards on the right
    deckList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
        if (newV != null) {
            Deck d = storage.loadDeck(newV);
            cardList.getItems().setAll(formatCards(d));
        }
    });

    // --- button behavior (unchanged logic you already had) ---
    add.setOnAction(e -> {
        String deckName = deckList.getSelectionModel().getSelectedItem();
        if (deckName == null) return;
        String q = qField.getText().trim();
        String a = aField.getText().trim();
        if (q.isEmpty() || a.isEmpty()) return;

        Deck d = storage.loadDeck(deckName);
        if (d == null) d = new Deck(deckName, new ArrayList<>());
        d.getCards().add(new Flashcard(q, a));
        storage.saveDeck(d);

        qField.clear();
        aField.clear();
        cardList.getItems().setAll(formatCards(d));
    });

    edit.setOnAction(e -> {
        String deckName = deckList.getSelectionModel().getSelectedItem();
        int idx = cardList.getSelectionModel().getSelectedIndex();
        if (deckName == null || idx < 0) return;

        Deck d = storage.loadDeck(deckName);
        if (d == null || idx >= d.getCards().size()) return;

        Flashcard current = d.getCards().get(idx);

        Dialog<Flashcard> dialog = new Dialog<>();
        dialog.setTitle("Edit Card");
        dialog.setHeaderText("Update the question and answer");

        Label qL = new Label("Question:");
        TextField qT = new TextField(current.getQuestion());
        Label aL = new Label("Answer:");
        TextField aT = new TextField(current.getAnswer());

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, qL, qT);
        gp.addRow(1, aL, aT);

        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                return new Flashcard(qT.getText(), aT.getText());
            }
            return null;
        });

        Flashcard updated = dialog.showAndWait().orElse(null);
        if (updated != null) {
            current.setQuestion(updated.getQuestion());
            current.setAnswer(updated.getAnswer());
            storage.saveDeck(d);
            cardList.getItems().setAll(formatCards(d));
        }
    });

    del.setOnAction(e -> {
        String deckName = deckList.getSelectionModel().getSelectedItem();
        int idx = cardList.getSelectionModel().getSelectedIndex();
        if (deckName == null || idx < 0) return;

        Deck d = storage.loadDeck(deckName);
        if (d == null || idx >= d.getCards().size()) return;

        d.getCards().remove(idx);
        storage.saveDeck(d);
        cardList.getItems().setAll(formatCards(d));
    });

    back.setOnAction(e -> stage.setScene(getPrimaryScene(stage)));

    MacShell shell = new MacShell(this, stage);
    return shell.sceneWith(center);
}


/**
 * STUDENT NOTE: What does `startQuizScene()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    public Scene startQuizScene(Stage stage) {
        StudentLogger.enter("names#startQuizScene");  // we just entered this method
        VBox center = new VBox(12);  // Object creation: why do we need a new instance here?
        Label title = new Label("Start Quiz");  // Object creation: why do we need a new instance here?
        title.getStyleClass().add("headline");  // We're adding to a collection; note the order & size.
        ComboBox<String> deckPicker = new ComboBox<>();  // Object creation: why do we need a new instance here?
        deckPicker.getItems().setAll(storage.listDeckNames());
        deckPicker.setPromptText("Select a deckObject to quiz");

        Button start = new Button("Start");  // Object creation: why do we need a new instance here?
        Button back = new Button("Back");  // Object creation: why do we need a new instance here?

        HBox row = new HBox(8, start, back);  // Object creation: why do we need a new instance here?

        start.setOnAction(exceptionObj -> {
            String selected = deckPicker.getSelectionModel().getSelectedItem();
            if (selected == null) return;  // Check the condition; what cases pass/fail?
            Deck d = storage.loadDeck(selected);
            if (d == null || d.getCards().isEmpty()) return;  // Check the condition; what cases pass/fail?
            QuizManager qm = new QuizManager(d);  // Object creation: why do we need a new instance here?
            qm.shuffle(); // shuffle before quiz
            stage.setScene(quizScene(stage, qm));
        });

        back.setOnAction(exceptionObj -> stage.setScene(getPrimaryScene(stage)));

        center.getChildren().addAll(title, deckPicker, row);
        MacShell shell = new MacShell(this, stage);  // Object creation: why do we need a new instance here?
        StudentLogger.step("About to return from method.");  // check what value we return next
        return shell.sceneWith(center);  // Return value: where is it used next?
    }

/**
 * STUDENT NOTE: What does `quizScene()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    private Scene quizScene(Stage stage, QuizManager qm) {
        StudentLogger.enter("names#quizScene");  // we just entered this method
        VBox center = new VBox(12);  // Object creation: why do we need a new instance here?

        Label progress = new Label();  // Object creation: why do we need a new instance here?
        Label question = new Label();  // Object creation: why do we need a new instance here?
        question.getStyleClass().add("question-label");  // We're adding to a collection; note the order & size.
        TextField answer = new TextField();  // Object creation: why do we need a new instance here?
        answer.setPromptText("Type your answer here");
        Button submit = new Button("Submit");  // Object creation: why do we need a new instance here?
        Label feedback = new Label();  // Object creation: why do we need a new instance here?

        Button next = new Button("Next");  // Object creation: why do we need a new instance here?
        next.setDisable(true);

        Runnable refresh = () -> {
            progress.setText("Question " + (qm.getIndex() + 1) + " of " + qm.size());
            question.setText(qm.currentQuestion());
            feedback.setText("");
            answer.clear();
            submit.setDisable(false);
            next.setDisable(true);
        };

        submit.setOnAction(exceptionObj -> {
            String a = answer.getText().trim();
            boolean correct = qm.submitAnswer(a);
            feedback.setText(correct ? "Correct!" : ("Incorrect. Correct answer: " + qm.currentAnswer()));
            submit.setDisable(true);
            next.setDisable(false);
        });

        next.setOnAction(exceptionObj -> {
            if (qm.hasNext()) {  // decision point — what happens inputFile each branch?
                qm.next();
                refresh.run();
            } else {
                stage.setScene(resultsScene(stage, qm));
            }
        });

        refresh.run();

        center.getChildren().addAll(progress, question, answer, submit, feedback, next);
        MacShell shell = new MacShell(this, stage);  // Object creation: why do we need a new instance here?
        StudentLogger.step("About to return from method.");  // check what value we return next
        return shell.sceneWith(center);  // Return value: where is it used next?
    }

/**
 * STUDENT NOTE: What does `resultsScene()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    private Scene resultsScene(Stage stage, QuizManager qm) {
        StudentLogger.enter("names#resultsScene");  // we just entered this method
        VBox center = new VBox(12);  // Object creation: why do we need a new instance here?
        Label done = new Label("Quiz Finished!");  // Object creation: why do we need a new instance here?
        done.getStyleClass().add("headline");  // We're adding to a collection; note the order & size.
        Label score = new Label("Score: " + qm.getScore() + " / " + qm.size());  // Object creation: why do we need a new instance here?
        Button retry = new Button("Retake (same deckObject, reshuffle)");  // Object creation: why do we need a new instance here?
        Button menu = new Button("Main Menu");  // Object creation: why do we need a new instance here?

        retry.setOnAction(exceptionObj -> {
            qm.resetAndShuffle();
            stage.setScene(quizScene(stage, qm));
        });
        menu.setOnAction(exceptionObj -> stage.setScene(getPrimaryScene(stage)));

        center.getChildren().addAll(done, score, retry, menu);
        MacShell shell = new MacShell(this, stage);  // Object creation: why do we need a new instance here?
        StudentLogger.step("About to return from method.");  // check what value we return next
        return shell.sceneWith(center);  // Return value: where is it used next?
    }

    // Utility: format cards as "Q -> A" strings for itemsList view
/**
 * STUDENT NOTE: What does `formatCards()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    private List<String> formatCards(Deck d) {
        StudentLogger.enter("names#formatCards");  // we just entered this method
        List<String> outputFile = new ArrayList<>();  // Object creation: why do we need a new instance here?
        if (d == null) return outputFile;  // Check the condition; what cases pass/fail?
        for (var c : d.getCards()) {  // loop — predict how many times this runs.
            outputFile.add("Q: " + c.getQuestion() + "  →  A: " + c.getAnswer());  // We're adding to a collection; note the order & size.
        }
        StudentLogger.step("About to return from method.");  // check what value we return next
        return outputFile;  // Return value: where is it used next?
    }
}