/*
 * ===============================================================
 * File: SidebarMain.java
 * ===============================================================
 */

package quizgame;
import quizgame.util.StudentLogger; 

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarMain {

    private final ScreenRouter router;

    public SidebarMain(ScreenRouter router) {
        StudentLogger.enter("names#SidebarMain");  
        this.router = router;
    }

    public Scene build(Stage stage) {
        StudentLogger.enter("names#build");  
        BorderPane root = new BorderPane();  

        // LEFT SIDEBAR
        VBox sidebar = new VBox(8);  
        sidebar.getStyleClass().add("sidebar");  
        sidebar.setPadding(new Insets(16));

        Label appTitle = new Label("Quizzits");  
        appTitle.getStyleClass().add("sidebar-title");  

        // "Home" item (selected look)
        Button home = sidebarItem("🏠", "Home");
        home.getStyleClass().add("sidebar-item-selected");  
        home.setOnAction(exceptionObj -> stage.setScene(router.getPrimaryScene(stage))); // reload self

        // Collapsible-ish "Start Quiz" (simple item)
        Button startQuiz = sidebarItem("▶", "Start Quiz");
        startQuiz.setOnAction(exceptionObj -> stage.setScene(router.startQuizScene(stage)));

        // Section header
        Label library = new Label("Library");  
        library.getStyleClass().add("sidebar-section");  

        Button createDeck = sidebarItem("≡", "Create Deck");
        createDeck.setOnAction(exceptionObj -> stage.setScene(router.createDeckScene(stage)));

        Button manageDecks = sidebarItem("✎", "Manage Decks");
        manageDecks.setOnAction(exceptionObj -> stage.setScene(router.manageDecksScene(stage)));

        Button exit = sidebarItem("✕", "Exit");
        exit.setOnAction(exceptionObj -> stage.close());

      //  Region spacer = new Region();  
      //  VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            appTitle,
            home,
            startQuiz,
       //     spacer,
            library,
            createDeck,
            manageDecks,
            exit
        );

        // CENTER CONTENT (simple welcome card)
        VBox center = new VBox(10);  
        center.setPadding(new Insets(24));
        Label welcome = new Label("Welcome to Quizzits");  
        welcome.getStyleClass().add("headline");  
        Label hint = new Label("Use the sidebar to Start Quiz or manage your decks.");  
        hint.getStyleClass().add("subtle");  
        center.getChildren().addAll(welcome, hint);

        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene = new Scene(root, 820, 520);  
        Styles.apply(scene);
        StudentLogger.step("About to return from method.");  
        return scene;  
    }

    

    private Button sidebarItem(String glyph, String text) {
        StudentLogger.enter("names#sidebarItem");  
        Label icon = new Label(glyph);  
        icon.getStyleClass().add("sidebar-icon");  
        Label label = new Label(text);  
        HBox box = new HBox(10, icon, label);  
        box.setAlignment(Pos.CENTER_LEFT);
        Button btn = new Button();  
        btn.setGraphic(box);
        btn.getStyleClass().add("sidebar-item");  
        btn.setMaxWidth(Double.MAX_VALUE);
        StudentLogger.step("About to return from method.");  
        return btn;  
    }


    

 
    public Region buildSidebar(Stage stage) {
        StudentLogger.enter("names#buildSidebar");  
        javafx.scene.layout.VBox sidebar = new javafx.scene.layout.VBox(8);  
        sidebar.getStyleClass().add("sidebar");  
        sidebar.setPadding(new javafx.geometry.Insets(16));

        javafx.scene.control.Label appTitle = new javafx.scene.control.Label("Quizzits");  
        appTitle.getStyleClass().add("sidebar-title");  

        javafx.scene.control.Button home = sidebarItem("🏠", "Home");
        home.getStyleClass().add("sidebar-item-selected");  
        home.setOnAction(exceptionObj -> stage.setScene(build(stage)));

        javafx.scene.control.Button startQuiz = sidebarItem("▶", "Start Quiz");
        startQuiz.setOnAction(exceptionObj -> stage.setScene(router.startQuizScene(stage)));

        javafx.scene.control.Label library = new javafx.scene.control.Label("Library");  
        library.getStyleClass().add("sidebar-section");  

        javafx.scene.control.Button createDeck = sidebarItem("≡", "Create Deck");
        createDeck.setOnAction(exceptionObj -> stage.setScene(router.createDeckScene(stage)));

        javafx.scene.control.Button manageDecks = sidebarItem("✎", "Manage Decks");
        manageDecks.setOnAction(exceptionObj -> stage.setScene(router.manageDecksScene(stage)));

        javafx.scene.control.Button exit = sidebarItem("✕", "Exit");
        exit.setOnAction(exceptionObj -> stage.close());

      //  javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();  
      //  javafx.scene.layout.VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        sidebar.getChildren().addAll(
            appTitle,
            home,
            startQuiz,
          //  spacer,
            library,
            createDeck,
            manageDecks,
            exit
        );
        StudentLogger.step("About to return from method.");  
        return sidebar;  
    }
}