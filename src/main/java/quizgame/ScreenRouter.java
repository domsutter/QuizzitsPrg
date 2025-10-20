/*
 * =======================  STUDENT NOTES  =======================
 * File: ScreenRouter.java
 * Central place that builds center content for each page and wraps it
 * with the mac-style shell so all screens have the same size/layout.
 * ===============================================================
 */
package quizgame;

import quizgame.util.StudentLogger;

import quizgame.model.Deck;
import quizgame.model.Flashcard;
import quizgame.model.QuizManager;
import quizgame.storage.StorageService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ScreenRouter {

    private final StorageService storage = new StorageService();

    // =========================== HOME ============================

    public Scene getPrimaryScene(Stage stage) {
        StudentLogger.enter("names#getPrimaryScene");
        // Let SidebarMain build a basic welcome scene via MacShell
        StudentLogger.step("About to return from method.");
        return new SidebarMain(this).build(stage);
    }

    // ====================== CREATE DECK ==========================

    public Scene createDeckScene(Stage stage) {
        StudentLogger.enter("names#createDeckScene");

        // Toolbar
        HBox toolbar = makeToolbar("Create Deck");

        // Card
        VBox card = makeCard();
        Label nameLabel = makeFieldLabel("Name");
        TextField deckName = new TextField();
        deckName.setPromptText("Enter deck name (must be unique)");

        // Buttons
        Button create = new Button("Create");
        create.getStyleClass().add("primary");
        Button back = new Button("Back");
        back.getStyleClass().add("secondary");

        HBox buttons = new HBox(8, back, create);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        // Status banner
        Label status = makeBanner();

        // Wire actions
        create.setOnAction(e -> {
            String name = deckName.getText().trim();
            if (name.isEmpty()) {
                showError(status, "Deck name cannot be empty.");
                return;
            }
            if (storage.deckExists(name)) {
                showError(status, "A deck with that name already exists.");
                return;
            }
            Deck d = new Deck(name, new ArrayList<>());
            boolean ok = storage.saveDeck(d);
            if (ok) {
                showOk(status, "Deck created successfully.");
                deckName.clear();
                deckName.requestFocus();
            } else {
                showError(status, "Failed to save deck.");
            }
        });

        back.setOnAction(e -> stage.setScene(getPrimaryScene(stage)));

        card.getChildren().addAll(nameLabel, deckName, buttons, status);

        VBox center = wrap(toolbar, card);
        MacShell shell = new MacShell(this, stage);
        StudentLogger.step("About to return from method.");
        return shell.sceneWith(center);
    }

    // ===================== MANAGE DECKS ==========================

    public Scene manageDecksScene(Stage stage) {
        StudentLogger.enter("names#manageDecksScene");

        // Toolbar
        HBox toolbar = makeToolbar("Manage Decks");

        // Card with two panes
        VBox card = makeCard();

        // Left: decks
        ListView<String> deckList = new ListView<>();
        deckList.setItems(FXCollections.observableArrayList(storage.listDeckNames()));
        deckList.setPrefWidth(220);

        // Right: cards of selected deck
        ListView<String> cardList = new ListView<>();

        // Inputs row
        TextField qField = new TextField();
        qField.setPromptText("Question");
        TextField aField = new TextField();
        aField.setPromptText("Answer");

        HBox inputsRow = new HBox(8, qField, aField);
        HBox.setHgrow(qField, Priority.ALWAYS);
        HBox.setHgrow(aField, Priority.ALWAYS);

        // Buttons row
        Button add  = new Button("Add Card");
        add.getStyleClass().add("primary");
        Button edit = new Button("Edit Card");
        Button del  = new Button("Delete Card");
        Button back = new Button("Back");
        back.getStyleClass().add("secondary");
        for (Button b : new Button[]{add, edit, del, back}) b.setMinWidth(110);

        HBox buttonsRow = new HBox(8, add, edit, del, back);
        buttonsRow.setAlignment(Pos.CENTER_LEFT);

        // Status banner
        Label status = makeBanner();

        // Two-column body
        HBox body = new HBox(12, deckList, cardList);
        HBox.setHgrow(cardList, Priority.ALWAYS);

        // Assemble card
        card.getChildren().addAll(inputsRow, buttonsRow, body, status);

        // Selection -> load cards
        deckList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                Deck d = storage.loadDeck(newV);
                cardList.getItems().setAll(formatCards(d));
                hideBanner(status);
            }
        });

        // Actions
        add.setOnAction(e -> {
            String deckName = deckList.getSelectionModel().getSelectedItem();
            if (deckName == null) { showError(status, "Select a deck first."); return; }
            String q = qField.getText().trim();
            String a = aField.getText().trim();
            if (q.isEmpty() || a.isEmpty()) { showError(status, "Enter a question and answer."); return; }

            Deck d = storage.loadDeck(deckName);
            if (d == null) d = new Deck(deckName, new ArrayList<>());
            d.getCards().add(new Flashcard(q, a));
            storage.saveDeck(d);

            qField.clear(); aField.clear();
            cardList.getItems().setAll(formatCards(d));
            showOk(status, "Card added.");
        });

        edit.setOnAction(e -> {
            String deckName = deckList.getSelectionModel().getSelectedItem();
            int idx = cardList.getSelectionModel().getSelectedIndex();
            if (deckName == null) { showError(status, "Select a deck first."); return; }
            if (idx < 0) { showError(status, "Select a card to edit."); return; }

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
                showOk(status, "Card updated.");
            }
        });

        del.setOnAction(e -> {
            String deckName = deckList.getSelectionModel().getSelectedItem();
            int idx = cardList.getSelectionModel().getSelectedIndex();
            if (deckName == null) { showError(status, "Select a deck first."); return; }
            if (idx < 0) { showError(status, "Select a card to delete."); return; }

            Deck d = storage.loadDeck(deckName);
            if (d == null || idx >= d.getCards().size()) return;

            d.getCards().remove(idx);
            storage.saveDeck(d);
            cardList.getItems().setAll(formatCards(d));
            showOk(status, "Card deleted.");
        });

        back.setOnAction(e -> stage.setScene(getPrimaryScene(stage)));

        VBox center = wrap(toolbar, card);
        MacShell shell = new MacShell(this, stage);
        return shell.sceneWith(center);
    }

    // ======================= START QUIZ ==========================

    public Scene startQuizScene(Stage stage) {
        StudentLogger.enter("names#startQuizScene");

        HBox toolbar = makeToolbar("Start Quiz");
        VBox card = makeCard();

        Label deckLabel = makeFieldLabel("Deck");
        ComboBox<String> deckPicker = new ComboBox<>();
        deckPicker.getItems().setAll(storage.listDeckNames());
        deckPicker.setPromptText("Select a deck");

        Button start = new Button("Start");
        start.getStyleClass().add("primary");
        Button back = new Button("Back");
        back.getStyleClass().add("secondary");

        HBox buttons = new HBox(8, back, start);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label status = makeBanner();

        start.setOnAction(e -> {
            String selected = deckPicker.getSelectionModel().getSelectedItem();
            if (selected == null) { showError(status, "Choose a deck first."); return; }
            Deck d = storage.loadDeck(selected);
            if (d == null || d.getCards().isEmpty()) { showError(status, "That deck has no cards."); return; }
            QuizManager qm = new QuizManager(d);
            qm.shuffle();
            stage.setScene(quizScene(stage, qm));
        });

        back.setOnAction(e -> stage.setScene(getPrimaryScene(stage)));

        card.getChildren().addAll(deckLabel, deckPicker, buttons, status);

        VBox center = wrap(toolbar, card);
        MacShell shell = new MacShell(this, stage);
        StudentLogger.step("About to return from method.");
        return shell.sceneWith(center);
    }

    // ========================== QUIZ =============================

    private Scene quizScene(Stage stage, QuizManager qm) {
        StudentLogger.enter("names#quizScene");

        HBox toolbar = makeToolbar("Quiz");
        VBox card = makeCard();

        Label progress = new Label();
        progress.getStyleClass().add("field-label");

        Label question = new Label();
        question.getStyleClass().add("question-label");

        TextField answer = new TextField();
        answer.setPromptText("Type your answer…");

        Button submit = new Button("Submit");
        submit.getStyleClass().add("primary");
        Button next = new Button("Next");
        next.getStyleClass().add("secondary");
        next.setDisable(true);

        HBox buttons = new HBox(8, next, submit);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label feedback = makeBanner(); // reuse banner styling

        Runnable refresh = () -> {
            progress.setText("Question " + (qm.getIndex() + 1) + " of " + qm.size());
            question.setText(qm.currentQuestion());
            hideBanner(feedback);
            answer.clear();
            submit.setDisable(false);
            next.setDisable(true);
        };

        submit.setOnAction(e -> {
            String a = answer.getText().trim();
            boolean correct = qm.submitAnswer(a);
            if (correct) {
                showOk(feedback, "Correct!");
            } else {
                showError(feedback, "Incorrect. Correct answer: " + qm.currentAnswer());
            }
            submit.setDisable(true);
            next.setDisable(false);
        });

        next.setOnAction(e -> {
            if (qm.hasNext()) {
                qm.next();
                refresh.run();
            } else {
                stage.setScene(resultsScene(stage, qm));
            }
        });

        refresh.run();

        card.getChildren().addAll(progress, question, answer, buttons, feedback);

        VBox center = wrap(toolbar, card);
        MacShell shell = new MacShell(this, stage);
        StudentLogger.step("About to return from method.");
        return shell.sceneWith(center);
    }

    // ========================= RESULTS ===========================

    private Scene resultsScene(Stage stage, QuizManager qm) {
        StudentLogger.enter("names#resultsScene");

        HBox toolbar = makeToolbar("Results");
        VBox card = makeCard();

        Label done = new Label("Quiz Finished!");
        done.getStyleClass().add("headline");

        Label score = new Label("Score: " + qm.getScore() + " / " + qm.size());

        Button retry = new Button("Retake");
        retry.getStyleClass().add("primary");
        Button menu = new Button("Main Menu");
        menu.getStyleClass().add("secondary");

        HBox buttons = new HBox(8, menu, retry);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        retry.setOnAction(e -> {
            qm.resetAndShuffle();
            stage.setScene(quizScene(stage, qm));
        });
        menu.setOnAction(e -> stage.setScene(getPrimaryScene(stage)));

        card.getChildren().addAll(done, score, buttons);

        VBox center = wrap(toolbar, card);
        MacShell shell = new MacShell(this, stage);
        StudentLogger.step("About to return from method.");
        return shell.sceneWith(center);
    }

    // ========================= HELPERS ===========================

    private HBox makeToolbar(String titleText) {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("toolbar");
        Label title = new Label(titleText);
        title.getStyleClass().add("headline");
        bar.getChildren().add(title);
        return bar;
    }

    private VBox makeCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setFillWidth(true);
        return card;
    }

    private Label makeFieldLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("field-label");
        return l;
    }

    private Label makeBanner() {
        Label b = new Label();
        b.getStyleClass().add("banner");
        b.setManaged(false);
        b.setVisible(false);
        return b;
    }

    private void showError(Label banner, String msg) {
        banner.getStyleClass().setAll("banner", "error");
        banner.setText(msg);
        banner.setManaged(true);
        banner.setVisible(true);
    }

    private void showOk(Label banner, String msg) {
        banner.getStyleClass().setAll("banner", "ok");
        banner.setText(msg);
        banner.setManaged(true);
        banner.setVisible(true);
    }

    private void hideBanner(Label banner) {
        banner.setManaged(false);
        banner.setVisible(false);
    }

    private VBox wrap(Region toolbar, Region card) {
        VBox center = new VBox(12, toolbar, card);
        center.setPadding(new Insets(24));
        return center;
    }

    private List<String> formatCards(Deck d) {
        StudentLogger.enter("names#formatCards");
        List<String> out = new ArrayList<>();
        if (d == null) return out;
        for (var c : d.getCards()) {
            out.add("Q: " + c.getQuestion() + "  →  A: " + c.getAnswer());
        }
        StudentLogger.step("About to return from method.");
        return out;
    }
}
