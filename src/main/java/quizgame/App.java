/*
 * ===============================================================
 * File: App.java
 * ===============================================================
 */


package quizgame;
import quizgame.util.StudentLogger;  // helper for step-by-step logs

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * --------------------------------------
 * Tries to load an FXML main menu. If not available,
 * it falls back to the original code-built menu so the app never breaks.
 */
public class App extends Application {

    private final ScreenRouter router = new ScreenRouter();  // Object creation: why do we need a new instance here?

    @Override
    public void start(Stage stage) {
        StudentLogger.enter("names#start");  // we just entered this method
        stage.setTitle("Quizzits (Student Edition)");
        Scene scene = router.getPrimaryScene(stage); // will try FXML first
        stage.setWidth(MacShell.WIDTH);
        stage.setHeight(MacShell.HEIGHT);
        stage.setMinWidth(MacShell.WIDTH);
        stage.setMinHeight(MacShell.HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] commandLineArguments) {
        StudentLogger.enter("names#main"); 
        launch();
    }
}