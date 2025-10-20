/*
 * ===============================================================
 * File: SidebarMain.java
 * ===============================================================
 */
package quizgame;

import quizgame.util.StudentLogger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene; // only for getWindow cast
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane; // not required but kept for future
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarMain {

    /** Which nav item should appear selected (blue pill). */
    public enum Active { HOME, START_QUIZ, CREATE_DECK, MANAGE_DECKS, NONE }

    /** Global selection state so new scenes know which item to highlight. */
    private static Active currentActive = Active.HOME;

    private final ScreenRouter router;

    public SidebarMain(ScreenRouter router) {
        StudentLogger.enter("names#SidebarMain");
        this.router = router;
    }

    /** Builds the Home scene (used by ScreenRouter#getPrimaryScene). */
    public javafx.scene.Scene build(Stage stage) {
        currentActive = Active.HOME;
        // The center content is built by ScreenRouter#getPrimaryScene,
        // so this class normally just builds sidebars. Keeping this here
        // for compatibility with earlier code that calls build(stage).
        // You can still show a simple welcome if desired.
        VBox center = new VBox(10);
        center.setPadding(new Insets(24));
        center.getChildren().add(new Label("Welcome to Quizzits"));
        MacShell shell = new MacShell(router, stage);
        return shell.sceneWith(center);
    }

    /** Builds just the sidebar (signature unchanged for callers like MacShell). */
    public VBox buildSidebar(Stage stage) {
        StudentLogger.enter("names#buildSidebar");

        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(16));

        Label appTitle = new Label("Quizzits");
        appTitle.getStyleClass().add("sidebar-title");

        Button home = sidebarItem("🏠", "Home");
        home.setOnAction(e -> {
            currentActive = Active.HOME;
            stage.setScene(router.getPrimaryScene(stage));
        });

        Button startQuiz = sidebarItem("▶", "Start Quiz");
        startQuiz.setOnAction(e -> {
            currentActive = Active.START_QUIZ;
            stage.setScene(router.startQuizScene(stage));
        });

        Label library = new Label("Library");
        library.getStyleClass().add("sidebar-section");

        Button createDeck = sidebarItem("≡", "Create Deck");
        createDeck.setOnAction(e -> {
            currentActive = Active.CREATE_DECK;
            stage.setScene(router.createDeckScene(stage));
        });

        Button manageDecks = sidebarItem("✎", "Manage Decks");
        manageDecks.setOnAction(e -> {
            currentActive = Active.MANAGE_DECKS;
            stage.setScene(router.manageDecksScene(stage));
        });

        Button exit = sidebarItem("✕", "Exit");
        exit.setOnAction(e -> stage.close());

        sidebar.getChildren().addAll(
               // appTitle,
                home,
                startQuiz,
                library,
                createDeck,
                manageDecks,
                exit
        );

        // Apply/restore selected pill
        applySelected(currentActive, home, startQuiz, createDeck, manageDecks);

        StudentLogger.step("About to return from method.");
        return sidebar;
    }

    /** Create one sidebar button (icon + text in an HBox). */
    private Button sidebarItem(String glyph, String text) {
        Label icon = new Label(glyph);
        icon.getStyleClass().add("sidebar-icon");
        Label label = new Label(text);

        HBox box = new HBox(10, icon, label);
        box.setAlignment(Pos.CENTER_LEFT);

        Button btn = new Button();
        btn.setGraphic(box);
        btn.getStyleClass().addAll("sidebar-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    /** Apply the 'sidebar-item-selected' class to the matching button. */
    private void applySelected(Active active, Button home, Button startQuiz,
                               Button createDeck, Button manageDecks) {
        home.getStyleClass().remove("sidebar-item-selected");
        startQuiz.getStyleClass().remove("sidebar-item-selected");
        createDeck.getStyleClass().remove("sidebar-item-selected");
        manageDecks.getStyleClass().remove("sidebar-item-selected");

        switch (active) {
            case HOME -> home.getStyleClass().add("sidebar-item-selected");
            case START_QUIZ -> startQuiz.getStyleClass().add("sidebar-item-selected");
            case CREATE_DECK -> createDeck.getStyleClass().add("sidebar-item-selected");
            case MANAGE_DECKS -> manageDecks.getStyleClass().add("sidebar-item-selected");
            case NONE -> {}
        }
    }
}
